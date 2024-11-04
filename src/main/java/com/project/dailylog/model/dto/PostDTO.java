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

    public static PostDTO fromEntity(Post post) {
        PostDTO dto = new PostDTO();
        dto.postId = post.getPostId();
        dto.userId = post.getUserId();
        dto.postTitle = post.getPostTitle();
        dto.postContent = post.getPostContent();
        dto.postVisible = post.getPostVisible();
        dto.createdAt = post.getCreatedAt();
        dto.lastUpdatedAt = post.getLastUpdatedAt();
        return dto;
    }
}