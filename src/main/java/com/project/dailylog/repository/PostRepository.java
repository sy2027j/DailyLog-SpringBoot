package com.project.dailylog.repository;

import com.project.dailylog.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);
    //Post findByPostId(Long postId);
    //void deleteByPostId(Long postId);
}
