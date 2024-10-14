package com.project.dailylog.security.handler;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = oAuth2User.getUser();

        String email = user.getEmail();
        String name = user.getName();
        String provider = user.getProvider();
        String role = user.getRole().getKey();

        System.err.println(provider + " User logged in: " + name + " (" + email + ") " + role);

        String targetUrl = UriComponentsBuilder.fromUriString("http://3.39.72.204/loginSuccess")
                .queryParam("email", email)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }
}
