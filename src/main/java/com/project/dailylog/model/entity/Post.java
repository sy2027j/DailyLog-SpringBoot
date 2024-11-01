package com.project.dailylog.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long postId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="post_title")
    private String postTitle;

    @Column(name="post_content")
    private String postContent;

    @Column(name="post_visible")
    private String postVisible;

    private LocalDateTime lastUpdatedAt;
}