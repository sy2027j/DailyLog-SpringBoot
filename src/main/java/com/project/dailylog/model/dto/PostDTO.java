package com.project.dailylog.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostDTO {
    private Long postId;
    private Long userId;
    private String postTitle;
    private String postContent;
    private String postVisible;
    private LocalDateTime lastUpdatedAt;
}