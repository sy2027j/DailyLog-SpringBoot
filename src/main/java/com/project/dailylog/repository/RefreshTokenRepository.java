package com.project.dailylog.repository;

import com.project.dailylog.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    RefreshToken findByRefreshToken(String token);
    void deleteByRefreshToken(String token);
    Boolean existsByRefreshToken(String token);
}
