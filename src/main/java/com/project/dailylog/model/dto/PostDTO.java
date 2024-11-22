package com.project.dailylog.model.dto;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.PostComments;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long postId;
    private String nickname;
    private String postTitle;
    private String postContent;
    private String postVisible;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private int likeCount;
    private List<PostComments> commentsList;

    public static PostDTO fromEntity(Post post) {
        return PostDTO.builder()
                .postId(post.getPostId())
                .nickname(post.getUser().getNickname())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .postVisible(post.getPostVisible())
                .createdAt(post.getCreatedAt())
                .lastUpdatedAt(post.getLastUpdatedAt())
                .likeCount(post.getLikeCount())
                .commentsList(post.getCommentList())
                .build();
    }
}