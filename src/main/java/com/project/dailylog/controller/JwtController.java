package com.project.dailylog.controller;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.request.LoginRequest;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.model.request.TokenRefreshRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.model.response.SingleResult;
import com.project.dailylog.repository.UserRepository;
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
    private final CustomUserDetailsService userDetailsService;
    private final ResponseService responseService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

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

    @PostMapping("/refresh")
    public CommonResult refreshAccessToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        /*User refreshTokenOpt = userRepository.findByRefreshToken(requestRefreshToken);

        if (refreshTokenOpt != null) {
            String newAccessToken = jwtUtil.createAccessToken(refreshTokenOpt.toLoginDTO());
            return responseService.getSingleResult(newAccessToken);
        } else {
            return responseService.getFailResult();
        }*/
            return responseService.getFailResult();
    }

    @PostMapping("/signup")
    public CommonResult signup(@RequestBody SignupRequest signupRequest) {
        userService.registerUser(signupRequest);
        return responseService.getSuccessResult();
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
        refreshTokenService.createRefreshToken(user, refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accessToken", accessToken);
        responseMap.put("userInfo", LoginDTO.builder()
                .nickname(user.getNickname())
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

    @PostMapping("/user")
    public SingleResult<?> getUserInfo(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        User user = optionalUser.orElseThrow(() -> new NoSuchElementException("User not found"));
        LoginDTO userDto = user.toLoginDTO();

        String accessToken = jwtUtil.createAccessToken(userDto);
        String refreshToken = jwtUtil.createRefreshToken(userDto.getId().toString());
        refreshTokenService.createRefreshToken(user, refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("accessToken", accessToken);
        responseMap.put("userInfo", userDto);

        return responseService.getSingleResult(responseMap);
    }
}