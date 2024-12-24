package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.QPostComments;
import com.project.dailylog.model.entity.QUser;
import com.project.dailylog.model.response.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<CommentResponse> findCommentsByPostId(Long postId) {
        QPostComments comment = QPostComments.postComments;
        QUser user = QUser.user;

        List<CommentResponse> result = queryFactory
                .select(Projections.fields(CommentResponse.class,
                        comment.commentId,
                        comment.commentText,
                        comment.upperComment.commentId.as("upperId"),
                        user.id.as("userId"),
                        user.nickname,
                        user.email.as("userEmail"),
                        user.profile.as("userProfile"),
                        comment.createdAt
                ))
                .from(comment)
                .leftJoin(comment.user, user)
                .where(comment.parentPost.postId.eq(postId))
                .orderBy(comment.createdAt.asc())
                .fetch();

        return buildCommentTree(result);
    }

    private List<CommentResponse> buildCommentTree(List<CommentResponse> comments) {
        Map<Long, CommentResponse> commentMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        for (CommentResponse commentDto : comments) {
            commentMap.put(commentDto.getCommentId(), commentDto);

            if (commentDto.getUpperId() == null) {
                rootComments.add(commentDto);
            } else {
                Long parentId = commentDto.getUpperId();
                CommentResponse parentComment = commentMap.get(parentId);
                if (parentComment != null) {
                    parentComment.getChildComments().add(commentDto);
                }
            }
        }

        return rootComments;
    }
}
