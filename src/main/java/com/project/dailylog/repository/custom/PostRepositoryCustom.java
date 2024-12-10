package com.project.dailylog.repository.custom;

import com.project.dailylog.model.response.PostSimpleResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    List<PostSimpleResponse> findBestPosts(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, Long userId);
    List<PostSimpleResponse> findPostsBySubscribedUsers(Long userId);
    PostSimpleResponse findPostWithDetailInfo(Long postId, Long userId);
}
