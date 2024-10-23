package com.project.dailylog.controller;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.request.LoginRequest;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.model.response.LoginResponse;
import com.project.dailylog.security.jwt.JwtUtil;
import com.project.dailylog.security.service.CustomUserDetailsService;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class JwtController {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private final UserService userService;

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

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.registerUser(signupRequest);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 실패: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginDTO loginDTO = LoginDTO.builder()
                .id(userDetails.getUser().getId())
                .email(userDetails.getUsername())
                .role(userDetails.getUser().getRole())
                .build();

        String accessToken = jwtUtil.createAccessToken(loginDTO);
        String refreshToken = jwtUtil.createRefreshToken(loginDTO.getId().toString());

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}