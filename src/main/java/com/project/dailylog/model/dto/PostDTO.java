package com.project.dailylog.model.dto;

import com.project.dailylog.model.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long postId;
    private Long userId;
    private String postTitle;
    private String postContent;
    private String postVisible;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private int likeCount;

    public static PostDTO fromEntity(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .postVisible(post.getPostVisible())
                .createdAt(post.getCreatedAt())
                .lastUpdatedAt(post.getLastUpdatedAt())
                .likeCount(post.getLikeCount())
                .build();
    }
}