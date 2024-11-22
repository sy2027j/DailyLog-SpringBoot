package com.project.dailylog.service;

import com.project.dailylog.exception.UnauthorizedAccessException;
import com.project.dailylog.model.entity.*;
import com.project.dailylog.model.request.CommentRequest;
import com.project.dailylog.repository.PostCommentRepository;
import com.project.dailylog.repository.PostRepository;
import com.project.dailylog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 작성 메서드
     */
    @Transactional
    public PostComments postComment(CommentRequest commentRequest, Long userId) {
        Post parentPost = postRepository.findById(Long.valueOf(commentRequest.getPostId()))
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        String upperId = commentRequest.getUpperId();
        PostComments upperComment = null;

        if (upperId != null && !upperId.isEmpty()) {
            Long upperCommentId = Long.valueOf(upperId);
            upperComment = postCommentRepository.findById(upperCommentId)
                    .orElseThrow(() -> new IllegalArgumentException("상위 댓글이 존재하지 않습니다."));
        }

        PostComments newComment = new PostComments(commentRequest.getCommentText(), parentPost, upperComment, user);
        return postCommentRepository.save(newComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PostComments comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("댓글 삭제 권한이 없습니다.");
        }

        postCommentRepository.delete(comment);
    }
}
