package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.*;
import com.project.dailylog.model.response.PostSimpleResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    private final QUser user = QUser.user;
    private final QPostLikes postLikes = QPostLikes.postLikes;
    private final QPostComments postComments = QPostComments.postComments;
    private final QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;

    private JPAQuery<PostSimpleResponse> basePostQuery() {
        return queryFactory
                .select(Projections.fields(PostSimpleResponse.class,
                        post.postId,
                        post.postTitle,
                        post.postContent,
                        post.createdAt,
                        post.lastUpdatedAt,
                        user.id.as("userId"),
                        user.nickname.as("authorNickname"),
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
    public PostSimpleResponse findPostWithDetailInfo(Long postId) {
        return basePostQuery()
                .where(post.postId.eq(postId))
                .groupBy(post.postId)
                .fetchOne();
    }

    @Override
    public List<PostSimpleResponse> findBestPosts(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return basePostQuery()
                .where(post.createdAt.between(startDate, endDate))
                .groupBy(post.postId)
                .orderBy(postLikes.countDistinct().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<PostSimpleResponse> findPostsBySubscribedUsers(Long userId) {
        return basePostQuery()
                .where(post.user.id.in(
                        JPAExpressions.select(userSubscribe.subscribedUser.id)
                                .from(userSubscribe)
                                .where(userSubscribe.user.id.eq(userId))
                ))
                .groupBy(post.postId)
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}
