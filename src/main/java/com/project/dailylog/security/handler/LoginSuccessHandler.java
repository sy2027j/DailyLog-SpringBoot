package com.project.dailylog.security.handler;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.security.jwt.JwtUtil;
import com.project.dailylog.security.user.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = oAuth2User.getUser();
        Map<String, Object> user = oAuth2User.getAttributes();
        LoginDTO loginDTO = oAuth2User.getUser();

        String email = user.getEmail();
        String name = user.getName();
        String provider = user.getProvider();
        String role = user.getRole().getKey();
        System.err.println(" User logged in: " + 'd' + " (" + loginDTO.getEmail() + ") " + loginDTO.getRole());

        System.err.println(provider + " User logged in: " + name + " (" + email + ") " + role);
        String username = authentication.getName(); // 사용자의 이름을 가져옵니다.
        String token = jwtUtil.createAccessToken(loginDTO); // JWT 생성

        response.setHeader("Authorization", "Bearer " + token); // 응답 헤더에 토큰 추가
        response.getWriter().write("로그인 성공: " + token); // 토큰을 응답 본문에 포함 (선택 사항)
        response.setStatus(HttpServletResponse.SC_OK); // HTTP 200 OK 상태

        String targetUrl = UriComponentsBuilder.fromUriString("http://3.39.72.204/loginSuccess")
                .queryParam("email", loginDTO.getEmail())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}
