package com.project.dailylog.service;

import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.UserSubscribe;
import com.project.dailylog.model.entity.UserSubscribeId;
import com.project.dailylog.model.response.SubscriptionResponse;
import com.project.dailylog.repository.UserRepository;
import com.project.dailylog.repository.UserSubscribeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserSubscribeService {

    private final UserSubscribeRepository userSubscribeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void subscribe(Long userId, Long subUserId) {
        UserSubscribeId id = new UserSubscribeId(userId, subUserId);

        User user = User.builder().id(userId).build();

        User subscribedUser = userRepository.findById(subUserId)
                .orElseThrow(() -> new IllegalArgumentException("구독한 사용자를 찾을 수 없습니다."));

        UserSubscribe userSubscribe = UserSubscribe.builder()
                .id(new UserSubscribeId(userId, subUserId))
                .user(user)
                .subscribedUser(subscribedUser)
                .build();

        userSubscribeRepository.save(userSubscribe);
    }

    @Transactional
    public void unsubscribe(Long userId, Long subUserId) {
        UserSubscribeId id = new UserSubscribeId(userId, subUserId);
        userSubscribeRepository.deleteById(id);
    }

    @Transactional
    public List<SubscriptionResponse> getSubscriptions(Long userId) {
        return userSubscribeRepository.findSubscriptions(userId);
    }

    @Transactional
    public List<SubscriptionResponse> getSubscribers(Long userId) {
        return userSubscribeRepository.findSubscribers(userId);
    }

}
