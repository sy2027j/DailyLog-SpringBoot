package com.project.dailylog.repository.custom;

import com.project.dailylog.model.entity.QPostComments;
import com.project.dailylog.model.entity.QUser;
import com.project.dailylog.model.response.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostCommentRepositoryImpl implements PostCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostCommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<CommentResponse> findCommentsByPostId(Long postId) {
        QPostComments comment = QPostComments.postComments;
        QUser user = QUser.user;

        List<CommentResponse> result = queryFactory
                .select(Projections.fields(CommentResponse.class,
                        comment.commentId,
                        comment.commentText,
                        comment.upperComment.commentId.as("upperId"),
                        comment.isCommentForComment,
                        comment.depth,
                        user.nickname,
                        comment.createdAt
                ))
                .from(comment)
                .leftJoin(comment.user, user)
                .where(comment.parentPost.postId.eq(postId))
                .orderBy(comment.depth.asc(), comment.createdAt.asc())
                .fetch();

        return buildCommentTree(result);
    }

    private List<CommentResponse> buildCommentTree(List<CommentResponse> comments) {
        Map<Long, CommentResponse> commentMap = new HashMap<>();
        List<CommentResponse> rootComments = new ArrayList<>();

        for (CommentResponse commentDto : comments) {
            commentMap.put(commentDto.getCommentId(), commentDto);

            if (commentDto.getDepth() == 0) {
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
