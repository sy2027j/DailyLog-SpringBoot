package com.project.dailylog.repository.custom;

import com.project.dailylog.model.response.UserHomeResponse;

public interface UserRepositoryCustom {
    UserHomeResponse getUserFollowInfo(Long userId);
}
