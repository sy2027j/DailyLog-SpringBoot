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
public class PostDetailResponse {
    private PostSimpleResponse post;
    private List<CommentResponse> comments;
}
