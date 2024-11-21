package com.project.dailylog.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String postId;
    private String commentText;
    private String upperId;
}
