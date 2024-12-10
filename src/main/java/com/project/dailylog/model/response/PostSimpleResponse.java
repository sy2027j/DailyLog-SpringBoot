package com.project.dailylog.model.response;

import com.project.dailylog.model.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSimpleResponse {
    private Long postId;
    private String postContent;
    private Long userId;
    private String authorNickname;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private String postVisible;
    private Long likeCount;
    private Long commentCount;
    private List<String> postImageUrls;
    private boolean likedByUser = false;

    public static PostSimpleResponse fromEntity(Post post) {
        return PostSimpleResponse.builder()
                .postId(post.getPostId())
                .postContent(post.getPostContent())
                .userId(post.getUser().getId())
                .authorNickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .lastUpdatedAt(post.getLastUpdatedAt())
                .postVisible(post.getPostVisible())
                .likeCount(post.getLikeCount())
                .commentCount((long) post.getCommentList().size())
                .build();
    }
}