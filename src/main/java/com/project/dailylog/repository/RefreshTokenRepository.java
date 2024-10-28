package com.project.dailylog.repository;

import com.project.dailylog.model.entity.RefreshToken;
import com.project.dailylog.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken findByRefreshToken(String token);
    void deleteByUserId(String userId);
}
