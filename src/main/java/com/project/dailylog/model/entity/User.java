package com.project.dailylog.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="email")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="password")
    private String password;

    @Column(name="nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name="profile")
    private String profile;

    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<UserSocialAccount> socialAccounts = new ArrayList<>();

    public User update() {
        this.lastLoginAt = LocalDateTime.now();
        return this;
    }

    public LoginDTO toLoginDTO() {
        LoginDTO userDTO = LoginDTO.builder()
                .id(this.id)
                .nickname(this.nickname)
                .email(this.email)
                .role(this.role)
                .build();
        return userDTO;
    }
}