package com.project.dailylog.controller;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.security.jwt.JwtUtil;
import com.project.dailylog.security.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@AllArgsConstructor
public class JwtController {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (jwtUtil.validateToken(refreshToken)) {
            String userId = jwtUtil.getUserId(refreshToken);
            LoginDTO loginUser = userDetailsService.loadUserByUsername(userId).getUser().toLoginDTO();

            if (loginUser != null) {
                String newAccessToken = jwtUtil.createAccessToken(loginUser);
                return ResponseEntity.ok()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}