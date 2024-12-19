package com.project.dailylog.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String commentText;
    private Long upperId;
    private Long userId;
    private String nickname;
    private String userProfile;
    private LocalDateTime createdAt;

    private List<CommentResponse> childComments = new ArrayList<>();
}
