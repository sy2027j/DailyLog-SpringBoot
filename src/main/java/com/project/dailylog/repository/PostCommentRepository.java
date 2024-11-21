package com.project.dailylog.repository;

import com.project.dailylog.model.entity.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComments, Long> {
}
