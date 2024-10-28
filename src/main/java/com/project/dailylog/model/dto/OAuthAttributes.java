package com.project.dailylog.model.dto;

import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.UserSocialAccount;
import com.project.dailylog.model.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String,Object> attributes;
    private String nameAttributeKey;
    private String id;
    private String nickname;
    private String email;
    private String profile;
    private String provider;
    private Role role = Role.USER;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 8;

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }

    @Builder
    public OAuthAttributes(Map<String,Object> attributes, String nameAttributeKey, String id, String nickname, String email, String profile, String provider){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes){
        if("naver".equals(registrationId)){
            return ofNaver(registrationId, userNameAttributeName, attributes);
        }
        if("kakao".equals(registrationId)){
            return ofKakao(registrationId, userNameAttributeName, attributes);
        }
        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    public User toUserEntity(){
        return User.builder()
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .role(role)
                .build();
    }

    public UserSocialAccount toUserSocialAccountEntity(User user){
        return UserSocialAccount.builder()
                .id(id)
                .user(user)
                .provider(provider)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String,Object> attributes) {
        Map<String, Object> naverAccount = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .attributes(naverAccount)
                .nameAttributeKey(userNameAttributeName)
                .id(naverAccount.get("id").toString())
                .nickname((String) naverAccount.get("name"))
                .email((String) naverAccount.get("email"))
                .profile((String) naverAccount.get("profile_image"))
                .provider(registrationId)
                .build();
    }

    private static OAuthAttributes ofKakao(String registrationId, String userNameAttributeName, Map<String,Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .id(attributes.get(userNameAttributeName).toString())
                .nickname((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .provider(registrationId)
                .build();
    }

    public static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String,Object> attributes) {
        return OAuthAttributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .id(attributes.get(userNameAttributeName).toString())
                .nickname(generateRandomString())
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider(registrationId)
                .build();
    }
}
