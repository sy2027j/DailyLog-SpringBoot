package com.project.dailylog.repository;

import com.project.dailylog.model.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);
    //void deleteByPostId(Long postId);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.postLiked pl " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.postId " +
            "ORDER BY COUNT(pl) DESC")
    List<Post> findBestPosts(@Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             Pageable pageable);
}
