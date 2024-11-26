package com.project.dailylog.service;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.model.entity.PostLikes;
import com.project.dailylog.repository.PostLikeRepository;
import com.project.dailylog.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public void postLike(Long postId, Long userId) {
        if (postLikeRepository.existsByPost_PostIdAndUserId(postId, userId)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));

        PostLikes postLike = PostLikes.builder()
                .post(post)
                .userId(userId)
                .build();
        postLikeRepository.save(postLike);
    }

    @Transactional
    public void deleteLike(Long postId, Long userId) {
        PostLikes postLike = postLikeRepository.findByPost_PostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시물입니다."));

        postLikeRepository.delete(postLike);
    }
}
