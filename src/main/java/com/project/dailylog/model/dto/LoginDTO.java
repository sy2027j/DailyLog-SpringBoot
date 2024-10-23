package com.project.dailylog.model.dto;

import com.project.dailylog.model.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LoginDTO {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private String profile;
    private LocalDateTime lastLoginAt;
}
