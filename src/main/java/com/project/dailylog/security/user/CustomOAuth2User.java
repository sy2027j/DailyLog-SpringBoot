package com.project.dailylog.security.user;

import com.project.dailylog.model.dto.OAuthAttributes;
import com.project.dailylog.model.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private OAuthAttributes attributes;
    private String id;
    private String name;
    private String email;
    private String profile;
    private String provider;
    private Role role;

    public CustomOAuth2User(OAuthAttributes attributes) {
        this.attributes = attributes;
        this.id = attributes.getId();
        this.name = attributes.getName();
        this.email = attributes.getEmail();
        this.profile = attributes.getProfile();
        this.provider = attributes.getProvider();
        this.role = attributes.getRole();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(attributes.getRole().toString()));
    }

    @Override
    public String getName() {
        return attributes.getName();
    }
}
