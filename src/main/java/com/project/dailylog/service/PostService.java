package com.project.dailylog.service;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.PostImage;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.UserSubscribeId;
import com.project.dailylog.model.request.PostWriteRequest;
import com.project.dailylog.model.response.CommentResponse;
import com.project.dailylog.model.response.PostDetailResponse;
import com.project.dailylog.model.response.PostSimpleResponse;
import com.project.dailylog.model.response.UserHomeResponse;
import com.project.dailylog.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final GcsService gcsService;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostImageRepository postImageRepository;
    private final UserSubscribeRepository postSubscribeRepository;

    @Transactional
    public void postWrite(PostWriteRequest writeRequest, List<MultipartFile> postImages, User user) throws IOException {
        Post post = Post.builder()
                .user(user)
                .postContent(writeRequest.getPostContent())
                .postVisible(writeRequest.getPostVisible())
                .build();
        postRepository.save(post);
        if (postImages != null && !postImages.isEmpty()) {
            for (MultipartFile image : postImages) {
                if (!image.isEmpty()) {
                    String imageUrl = gcsService.upload(image);
                    postImageRepository.save(PostImage.builder()
                            .post(post)
                            .imageUrl(imageUrl)
                            .build());
                }
            }
        }
    }

    @Transactional
    public void postDelete(Long postId) throws Exception {
        postRepository.deleteById(postId);
    }

    @Transactional
    public List<PostSimpleResponse> getAllPost(Long userId) throws Exception {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "lastUpdatedAt")));;
        return posts.stream()
                .map(post -> {
                    PostSimpleResponse response = PostSimpleResponse.fromEntity(post);
                    if(userId != null) {
                        boolean isLiked = postLikeRepository.existsByPost_PostIdAndUserId(userId, post.getPostId());
                        response.setLikedByUser(isLiked);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public UserHomeResponse getPostsByUser(String targetUserEmail, User requestingUser) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(targetUserEmail);
        User targetUser = optionalUser.orElseThrow(() -> new NoSuchElementException("User not found"));

        Long targetId = targetUser.getId();
        Long requestingUserId = (requestingUser != null) ? requestingUser.getId() : null;
        UserHomeResponse userHomeResponse = userRepository.getUserFollowInfo(targetId);

        if(requestingUserId != null) {
            UserSubscribeId subscribeId = new UserSubscribeId(requestingUserId, targetId);
            userHomeResponse.setIsFollowing(postSubscribeRepository.existsById(subscribeId));
        }

        boolean isTargetSameAsRequester = (requestingUserId != null && requestingUserId.equals(targetId));
        boolean isFollowing = (requestingUserId != null && userHomeResponse.getIsFollowing());

        List<Post> posts = postRepository.findByUserId(targetId).stream()
                .filter(post -> {
                    String visibility = post.getPostVisible();
                    if (isTargetSameAsRequester) {
                        return true;
                    } else if ("public".equals(visibility)) {
                        return true;
                    } else if ("friends".equals(visibility) && isFollowing) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        List<PostSimpleResponse> postSimpleResponseList = posts.stream()
                .map(post -> {
                    PostSimpleResponse response = PostSimpleResponse.fromEntity(post);
                    if(requestingUserId != null) {
                        boolean isLiked = postLikeRepository.existsByPost_PostIdAndUserId(post.getPostId(), requestingUserId);
                        response.setLikedByUser(isLiked);
                    }
                    return response;
                })
                .collect(Collectors.toList());
        userHomeResponse.setPosts(postSimpleResponseList);

        return userHomeResponse;
    }

    @Transactional
    public PostDetailResponse getPostById(Long postId, Long userId) throws Exception {
        PostSimpleResponse postSimpleResponse = postRepository.findPostWithDetailInfo(postId, userId);
        List<CommentResponse> postComments = postCommentRepository.findCommentsByPostId(postId);
        return new PostDetailResponse(postSimpleResponse, postComments);
    }

    @Transactional
    public List<PostSimpleResponse> getNeighborPost(Long userId) throws Exception {
        List<PostSimpleResponse> posts = postRepository.findPostsBySubscribedUsers(userId);;
        return posts.stream()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostSimpleResponse> getBestPosts(String period, Long userId) throws Exception {
        LocalDateTime[] dateRange = calculateDateRange(period);
        return postRepository.findBestPosts(dateRange[0], dateRange[1], PageRequest.of(0, 20), userId);
    }

    private LocalDateTime[] calculateDateRange(String period) {
        if (period.equals("week")) {
            LocalDateTime weekStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
            LocalDateTime weekEnd = LocalDate.now().with(DayOfWeek.SUNDAY).atTime(23, 59, 59);

            return new LocalDateTime[]{weekStart, weekEnd};
        } else {
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);
            return new LocalDateTime[]{monthStart, monthEnd};
        }
    }
}
