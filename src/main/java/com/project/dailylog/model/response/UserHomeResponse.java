package com.project.dailylog.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHomeResponse {
    private Long userId;
    private String nickname;
    private String profile;
    private Long followers;
    private Long following;
    private Boolean isFollowing = false;
    private List<PostSimpleResponse> posts;
}
