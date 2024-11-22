package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.*;
import com.project.dailylog.model.response.PostResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public PostResponse findPostWithLikesAndUser(Long postId) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        QPostLikes postLikes = QPostLikes.postLikes;

        return queryFactory
                .select(Projections.fields(PostResponse.class,
                        post.postId,
                        post.postTitle,
                        post.postContent,
                        post.createdAt,
                        post.lastUpdatedAt,
                        user.nickname.as("authorNickname"),
                        ExpressionUtils.as(
                                JPAExpressions.select(postLikes.count())
                                        .from(postLikes)
                                        .where(postLikes.post.eq(post)),
                                "likeCount")
                ))
                .from(post)
                .leftJoin(post.user, user)
                .where(post.postId.eq(postId))
                .fetchOne();
    }

    @Override
    public List<Post> findBestPosts(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QPost post = QPost.post;
        QPostLikes postLikes = QPostLikes.postLikes;

        return queryFactory
                .select(post)
                .from(post)
                .leftJoin(post.postLiked, postLikes)
                .where(post.createdAt.between(startDate, endDate))
                .groupBy(post.postId)
                .orderBy(postLikes.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Post> findPostsBySubscribedUsers(Long userId) {
        QPost post = QPost.post;
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;

        return queryFactory
                .selectFrom(post)
                .where(post.user.id.in(
                        JPAExpressions.select(userSubscribe.subscribedUser.id)
                                .from(userSubscribe)
                                .where(userSubscribe.user.id.eq(userId))
                ))
                .fetch();
    }
}
