package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.QPost;
import com.project.dailylog.model.entity.QPostLikes;
import com.project.dailylog.model.entity.QUserSubscribe;
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
                .where(post.userId.in(
                        JPAExpressions.select(userSubscribe.subscribedUser.id)
                                .from(userSubscribe)
                                .where(userSubscribe.user.id.eq(userId))
                ))
                .fetch();
    }
}
