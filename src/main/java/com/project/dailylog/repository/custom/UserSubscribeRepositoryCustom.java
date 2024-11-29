package com.project.dailylog.repository.custom;

import com.project.dailylog.model.response.SubscriptionResponse;

import java.util.List;

public interface UserSubscribeRepositoryCustom {
    List<SubscriptionResponse> findSubscriptions(Long userId);
    List<SubscriptionResponse> findSubscribers(Long userId);
}
