package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.QUser;
import com.project.dailylog.model.entity.QUserSubscribe;
import com.project.dailylog.model.response.SubscriptionResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserSubscribeRepositoryCustomImpl implements UserSubscribeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SubscriptionResponse> findSubscriptions(Long userId) {
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;
        QUser user = QUser.user;

        return queryFactory
                .select(Projections.fields(SubscriptionResponse.class,
                        user.id,
                        user.nickname,
                        user.profile
                ))
                .from(userSubscribe)
                .join(userSubscribe.subscribedUser, user)
                .where(userSubscribe.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<SubscriptionResponse> findSubscribers(Long userId) {
        QUserSubscribe userSubscribe = QUserSubscribe.userSubscribe;
        QUser user = QUser.user;

        return queryFactory
                .select(Projections.fields(SubscriptionResponse.class,
                        user.id,
                        user.nickname,
                        user.profile
                ))
                .from(userSubscribe)
                .join(userSubscribe.user, user)
                .where(userSubscribe.subscribedUser.id.eq(userId))
                .fetch();
    }
}
