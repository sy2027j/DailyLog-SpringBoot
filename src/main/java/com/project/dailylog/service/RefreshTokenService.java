package com.project.dailylog.service;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.RefreshToken;
import com.project.dailylog.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(token)
                .userId(user.getId().toString())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId().toString());
    }
}

