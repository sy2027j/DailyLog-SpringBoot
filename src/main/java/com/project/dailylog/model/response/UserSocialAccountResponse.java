package com.project.dailylog.model.response;

import com.project.dailylog.model.entity.UserSocialAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSocialAccountResponse {
    private String provider;
    private LocalDateTime lastLoginAt;

    public static UserSocialAccountResponse fromEntity(UserSocialAccount entity) {
        return new UserSocialAccountResponse(
                entity.getProvider(),
                entity.getLastLoginAt()
        );
    }
}
