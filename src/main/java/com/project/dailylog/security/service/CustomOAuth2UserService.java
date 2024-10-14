package com.project.dailylog.security.service;

import com.project.dailylog.model.dto.OAuthAttributes;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.repository.UserRepository;
import com.project.dailylog.security.user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        System.err.println(registrationId);
        System.err.println(userNameAttributeName);
        System.err.println(oAuth2User.getAttributes());

        User user = saveOrUpdate(registrationId, attributes);

        //return new DefaultOAuth2User(
        //        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
        //        attributes.getAttributes(),
        //        attributes.getNameAttributeKey());

        return new CustomOAuth2User(user, attributes.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findById(attributes.getId())
                .map(entity -> entity.update(attributes.getName(), attributes.getProfile()))
    private User saveOrUpdate(String provider, OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getEmail(), attributes.getName(), attributes.getProfile(), provider))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }

}