package com.project.dailylog.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "post_comment")
public class PostComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long commentId;

    @Column
    String commentText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    Post parentPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="upper_id")
    PostComments upperComment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    User user;

    @Column(name = "is_comment_for_comment")
    Boolean isCommentForComment;

    Integer depth;

    @Column(name = "order_number")
    Long orderNumber;

    public PostComments(String commentText, Post parentPost, PostComments upperComment , User user) {
        this.commentText = commentText;
        this.parentPost = parentPost;
        this.upperComment = upperComment;
        this.user = user;
        if (upperComment == null) {
            this.isCommentForComment = false;
            this.depth = 0 ;
            this.orderNumber = parentPost.getCommentCount();
        } else {
            this.isCommentForComment = true;
            this.depth = upperComment.depth+1;
            this.orderNumber = upperComment.getOrderNumber();
        }
    }

    public void updateCommentText(String commentText) {
        this.commentText = commentText;
    }

}