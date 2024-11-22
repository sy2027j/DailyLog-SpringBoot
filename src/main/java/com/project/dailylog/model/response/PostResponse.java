package com.project.dailylog.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long postId;
    private String postTitle;
    private String postContent;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private String authorNickname;
    private Long likeCount;
}
