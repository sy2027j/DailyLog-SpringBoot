package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.QUser;
import com.project.dailylog.model.entity.QUserSubscribe;
import com.project.dailylog.model.response.UserHomeResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public UserHomeResponse getUserFollowInfo(Long userId) {
        QUser user = QUser.user;
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;

        return queryFactory
                .select(Projections.fields(UserHomeResponse.class,
                        user.id.as("userId"),
                        user.nickname,
                        user.profile,
                        ExpressionUtils.as(
                                JPAExpressions.select(userSubscribe.count())
                                        .from(userSubscribe)
                                        .where(userSubscribe.subscribedUser.id.eq(userId)),
                                "followers"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(userSubscribe.count())
                                        .from(userSubscribe)
                                        .where(userSubscribe.user.id.eq(userId)),
                                "following"
                        )
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }
}
