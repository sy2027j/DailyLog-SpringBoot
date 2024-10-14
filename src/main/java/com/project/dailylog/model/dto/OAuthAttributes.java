package com.project.dailylog.model.dto;

import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String,Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String profile;
    private String provider;

    @Builder
    public OAuthAttributes(Map<String,Object> attributes, String nameAttributeKey, String name, String email, String profile, String provider){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes){
        if("naver".equals(registrationId)){
            return ofNaver("id",attributes, registrationId);
        }
        if("kakao".equals(registrationId)){
            return ofKakao("id",attributes, registrationId);
        }
        return ofGoogle(userNameAttributeName, attributes, registrationId);
    }

    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .role(Role.USER)
                .provider(provider)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes, String registrationId) {
        Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) naverAccount.get("name"))
                .profile((String) naverAccount.get("profile_image"))
                .email((String) naverAccount.get("email"))
                .attributes(naverAccount)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes, String registrationId) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .profile((String) profile.get("profile_image_url"))
                .email((String) kakaoAccount.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String,Object> attributes, String registrationId){
        return OAuthAttributes.builder()
                .profile((String) attributes.get("picture"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider(registrationId)
                .build();
    }
}
