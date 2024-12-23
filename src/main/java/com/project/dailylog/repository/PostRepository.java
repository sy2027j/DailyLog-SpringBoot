package com.project.dailylog.repository;

import com.project.dailylog.model.entity.Post;
import com.project.dailylog.repository.custom.PostRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @EntityGraph(attributePaths = {"postImages"})
    List<Post> findByUserId(Long userId);
    //void deleteByPostId(Long postId);
}
