package com.project.dailylog.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostWriteRequest {
    private String postTitle;
    private String postContent;
    private String postVisible;
    private String accessToken;
    private String refreshToken;
}
