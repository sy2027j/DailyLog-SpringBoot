package com.project.dailylog.repository.custom;

import com.project.dailylog.model.response.CommentResponse;

import java.util.List;

public interface PostCommentRepositoryCustom {
    public List<CommentResponse> findCommentsByPostId(Long postId);
}
