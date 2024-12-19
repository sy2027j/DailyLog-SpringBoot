package com.project.dailylog.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailInfoResponse {
    private Long userId;
    private String nickname;
    private String email;
    private String profile;
    private List<UserSocialAccountResponse> socialAccounts;
}
