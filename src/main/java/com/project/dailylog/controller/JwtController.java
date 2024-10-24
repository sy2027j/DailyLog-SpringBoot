package com.project.dailylog.controller;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.request.LoginRequest;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.model.response.LoginResponse;
import com.project.dailylog.model.response.SingleResult;
import com.project.dailylog.security.jwt.JwtUtil;
import com.project.dailylog.security.service.CustomUserDetailsService;
import com.project.dailylog.security.user.CustomUserDetails;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class JwtController {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ResponseService responseService;
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
    public CommonResult signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.registerUser(signupRequest);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            return responseService.getFailResult();
        }
    }

    @PostMapping("/login")
    public SingleResult<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
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

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accessToken", accessToken);
        responseMap.put("userInfo", LoginDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .profile(user.getProfile())
                .build());

        return responseService.getSingleResult(responseMap);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}