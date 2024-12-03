package com.project.dailylog.service;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.request.PostWriteRequest;
import com.project.dailylog.model.response.CommentResponse;
import com.project.dailylog.model.response.PostDetailResponse;
import com.project.dailylog.model.response.PostSimpleResponse;
import com.project.dailylog.repository.PostCommentRepository;
import com.project.dailylog.repository.PostRepository;
import com.project.dailylog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    private PostRepository postRepository;
    private UserRepository userRepository;
    private PostCommentRepository postCommentRepository;

    @Transactional
    public void postWrite(PostWriteRequest postWriteRequest, User user) throws Exception {
        postRepository.save(Post.builder()
                .user(user)
                .postTitle(postWriteRequest.getPostTitle())
                .postContent(postWriteRequest.getPostContent())
                .postVisible(postWriteRequest.getPostVisible())
                .build());
    }

    @Transactional
    public void postDelete(Long postId) throws Exception {
        postRepository.deleteById(postId);
    }

    @Transactional
    public List<PostSimpleResponse> getAllPost() throws Exception {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "lastUpdatedAt")));;
        return posts.stream()
                .map(PostSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostSimpleResponse> getPostsByUser(String userEmail) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new NoSuchElementException("User not found"));
        List<Post> posts = postRepository.findByUserId(user.getId());
        return posts.stream()
                .map(PostSimpleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostDetailResponse getPostById(Long postId) throws Exception {
        PostSimpleResponse postSimpleResponse = postRepository.findPostWithDetailInfo(postId);
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
    public List<PostSimpleResponse> getBestPosts(String period) throws Exception {
        LocalDateTime[] dateRange = calculateDateRange(period);
        return postRepository.findBestPosts(dateRange[0], dateRange[1], PageRequest.of(0, 20));
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
