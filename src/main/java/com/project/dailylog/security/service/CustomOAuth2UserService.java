package com.project.dailylog.security.service;

import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.dto.OAuthAttributes;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.UserSocialAccount;
import com.project.dailylog.repository.UserRepository;
import com.project.dailylog.repository.UserSocialAccountRepository;
import com.project.dailylog.security.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository userSocialAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);

        LoginDTO userDTO = LoginDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return new CustomOAuth2User(userDTO, attributes.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update())
                .orElse(attributes.toUserEntity());
        userRepository.save(user);

        UserSocialAccount socialAccount = userSocialAccountRepository.findByIdAndUser(attributes.getId(), user)
                .map(entity -> entity.update())
                .orElse(attributes.toUserSocialAccountEntity(user));
        userSocialAccountRepository.save(socialAccount);

        return user;
    }

}