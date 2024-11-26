package com.project.dailylog.repository;

import com.project.dailylog.model.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikes, Long> {
    boolean existsByPost_PostIdAndUserId(Long postId, Long userId);
    Optional<PostLikes> findByPost_PostIdAndUserId(Long postId, Long userId);
}
