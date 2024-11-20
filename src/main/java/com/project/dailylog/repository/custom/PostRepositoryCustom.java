package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.Post;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findBestPosts(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<Post> findPostsBySubscribedUsers(Long userId);
}
