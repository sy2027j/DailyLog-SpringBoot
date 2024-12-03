package com.project.dailylog.controller;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.request.LoginRequest;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.model.response.ErrorResult;
import com.project.dailylog.security.jwt.JwtUtil;
import com.project.dailylog.security.service.CustomUserDetailsService;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.RefreshTokenService;
import com.project.dailylog.service.ResponseService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class JwtController {
    private final JwtUtil jwtUtil;
    private final ResponseService responseService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResult(false, 401, "Refresh Token이 누락되었습니다.", "TOKEN_MISSING"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResult(false, 401, "Refresh Token이 유효하지 않습니다.", "INVALID_TOKEN"));
        }

        if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResult(false, 401, "Refresh Token이 DB에서 유효하지 않습니다.", "TOKEN_NOT_FOUND"));
        }

        String userId = jwtUtil.getUserId(refreshToken);
        LoginDTO loginUser = userDetailsService.loadUserByUserId(userId).getUser().toLoginDTO();

        if (loginUser != null) {
            refreshTokenService.deleteRefreshToken(refreshToken);

            String newAccessToken = jwtUtil.createAccessToken(loginUser);
            String newRefreshToken = jwtUtil.createRefreshToken(userId);

            refreshTokenService.createRefreshToken(userId, newRefreshToken);

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + newAccessToken)
                    .header("Set-Cookie", "refreshToken=" + newRefreshToken + "; HttpOnly; Secure")
                    .build();
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

    @PostMapping("/signup")
    public CommonResult signup(@RequestBody SignupRequest signupRequest) {
        userService.registerUser(signupRequest);
        return responseService.getSuccessResult();
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
        User user = userDetails.getUser();
        LoginDTO loginDTO = LoginDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        String accessToken = jwtUtil.createAccessToken(loginDTO);
        String refreshToken = jwtUtil.createRefreshToken(loginDTO.getId().toString());
        refreshTokenService.createRefreshToken(user.getId().toString(), refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userInfo", LoginDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .role(user.getRole())
                .profile(user.getProfile())
                .build());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .body(responseService.getSingleResult(responseMap));
    }

    @PostMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginDTO userDto = userService.getUserInfo(loginRequest.getEmail());

        String accessToken = jwtUtil.createAccessToken(userDto);
        String refreshToken = jwtUtil.createRefreshToken(userDto.getId().toString());
        refreshTokenService.createRefreshToken(userDto.getId().toString(), refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userInfo", LoginDTO.builder()
                .nickname(userDto.getNickname())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .profile(userDto.getProfile())
                .build());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .body(responseService.getSingleResult(responseMap));
    }

    @PostMapping("/logout")
    public CommonResult logout(@AuthenticationPrincipal CustomUserDetails user) {
        refreshTokenService.logout(user.getUsername());
        return responseService.getSuccessResult();
    }
}