package com.project.dailylog.service;

import com.project.dailylog.model.entity.RefreshToken;
import com.project.dailylog.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(String userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .userId(userId)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validateRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByRefreshToken(token);
    }

    @Transactional
    public boolean isRefreshTokenValid(String token) {
        return refreshTokenRepository.existsByRefreshToken(token);
    }
}

