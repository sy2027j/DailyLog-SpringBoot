package com.project.dailylog.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name="post_title")
    private String postTitle;

    @Column(name="post_content")
    private String postContent;

    @Column(name="post_visible")
    private String postVisible;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "post")
    private final List<PostLikes> postLiked = new ArrayList<>();

    @OneToMany(mappedBy = "parentPost")
    private final List<PostComments> postComments = new ArrayList<>();

    public int getLikeCount() {
        return postLiked.size();
    }

    public List<PostComments> getCommentList() {
        return Collections.unmodifiableList(postComments);
    }

    public Long getCommentCount() { return (long) postComments.size(); }
}