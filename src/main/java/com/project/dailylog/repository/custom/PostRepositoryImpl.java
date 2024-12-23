package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.QPost;
import com.project.dailylog.model.entity.QPostComments;
import com.project.dailylog.model.entity.QPostLikes;
import com.project.dailylog.model.entity.QUser;
import com.project.dailylog.model.entity.QPostImage;
import com.project.dailylog.model.entity.QUserSubscribe;
import com.project.dailylog.model.response.PostSimpleResponse;
import com.project.dailylog.repository.PostLikeRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    private final QUser user = QUser.user;
    private final QPostLikes postLikes = QPostLikes.postLikes;
    private final QPostComments postComments = QPostComments.postComments;
    private final QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;
    private final QPostImage postImage = QPostImage.postImage;
    private final PostLikeRepository postLikeRepository;

    private JPAQuery<PostSimpleResponse> basePostQuery() {
        return queryFactory
                .select(Projections.fields(PostSimpleResponse.class,
                        post.postId,
                        post.postContent,
                        post.createdAt,
                        post.lastUpdatedAt,
                        user.id.as("userId"),
                        user.nickname.as("authorNickname"),
                        user.email,
                        user.profile.as("authorProfile"),
                        post.postVisible,
                        postLikes.countDistinct().as("likeCount"),
                        postComments.countDistinct().as("commentCount")
                ))
                .from(post)
                .leftJoin(post.user, user)
                .leftJoin(postLikes).on(post.postId.eq(postLikes.post.postId))
                .leftJoin(postComments).on(post.postId.eq(postComments.parentPost.postId));
    }

    @Override
    public PostSimpleResponse findPostWithDetailInfo(Long postId, Long userId) {
        PostSimpleResponse postSimpleResponse = basePostQuery()
                .where(post.postId.eq(postId))
                .groupBy(post.postId)
                .fetchOne();

        List<String> imageResults = queryFactory
                .select(postImage.imageUrl)
                .from(postImage)
                .where(postImage.post.postId.eq(postId))
                .fetch();
        postSimpleResponse.setPostImageUrls(imageResults);

        if (userId != null) {
            System.err.println("상세조회: " + userId);
            boolean likedByUser = postLikeRepository.existsByPost_PostIdAndUserId(userId, postId);
            System.err.println(likedByUser);
            postSimpleResponse.setLikedByUser(likedByUser);
        }

        return postSimpleResponse;
    }

    @Override
    public List<PostSimpleResponse> findBestPosts(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, Long userId) {
        List<PostSimpleResponse> postResponses = basePostQuery()
                .where(post.createdAt.between(startDate, endDate))
                .groupBy(post.postId)
                .orderBy(postLikes.countDistinct().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        postResponses = fetchPostImages(postResponses);
        if (userId != null) {
            postResponses = getLikedPosts(postResponses, userId);
        }

        return postResponses;
    }

    @Override
    public List<PostSimpleResponse> findPostsBySubscribedUsers(Long userId) {
        List<PostSimpleResponse> postResponses = basePostQuery()
                .where(post.user.id.in(
                        JPAExpressions.select(userSubscribe.subscribedUser.id)
                                .from(userSubscribe)
                                .where(userSubscribe.user.id.eq(userId))
                ))
                .groupBy(post.postId)
                .orderBy(post.createdAt.desc())
                .fetch();

        postResponses = fetchPostImages(postResponses);
        postResponses = getLikedPosts(postResponses, userId);

        return postResponses;
    }

    private List<PostSimpleResponse> fetchPostImages(List<PostSimpleResponse> postResponses) {
        Set<Long> postIds = postResponses.stream()
                .map(PostSimpleResponse::getPostId)
                .collect(Collectors.toSet());

        List<Tuple> imageResults = queryFactory
                .select(postImage.post.postId, postImage.imageUrl)
                .from(postImage)
                .where(postImage.post.postId.in(postIds))
                .fetch();

        Map<Long, List<String>> postImages = new HashMap<>();

        imageResults.forEach(tuple -> {
            Long postId = tuple.get(postImage.post.postId);
            String imageUrl = tuple.get(postImage.imageUrl);
            postImages.computeIfAbsent(postId, k -> new ArrayList<>()).add(imageUrl);
        });

        postResponses.forEach(postResponse ->
                postResponse.setPostImageUrls(postImages.getOrDefault(postResponse.getPostId(), new ArrayList<>()))
        );

        return postResponses;
    }

    public List<PostSimpleResponse> getLikedPosts(List<PostSimpleResponse> postResponses, Long userId) {
        Set<Long> postIds = postResponses.stream()
                .map(PostSimpleResponse::getPostId)
                .collect(Collectors.toSet());

        List<Tuple> results = queryFactory
                .select(postLikes.post.postId, postLikes.count())
                .from(postLikes)
                .where(postLikes.userId.eq(userId)
                        .and(postLikes.post.postId.in(postIds)))
                .groupBy(postLikes.post.postId)
                .fetch();

        Map<Long, Boolean> likedPosts = results.stream()
                .collect(Collectors.toMap(tuple -> tuple.get(postLikes.post.postId), tuple -> tuple.get(postLikes.count()) > 0));

        postResponses.forEach(postResponse -> {
            Boolean likedByUser = likedPosts.get(postResponse.getPostId());
            postResponse.setLikedByUser(likedByUser != null && likedByUser);
        });

        return postResponses;
    }

}
