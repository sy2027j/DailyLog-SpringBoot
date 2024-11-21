package com.project.dailylog.controller;

import com.project.dailylog.model.request.CommentRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.PostCommentService;
import com.project.dailylog.service.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class PostCommentController {

    private PostCommentService postCommentService;
    private final ResponseService responseService;

    @PostMapping
    public CommonResult postComment(@RequestBody CommentRequest commentRequest, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postCommentService.postComment(commentRequest, userDetails.getUser().getId());
        return responseService.getSuccessResult();
    }

    @DeleteMapping("/{commentId}")
    public CommonResult deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postCommentService.deleteComment(commentId, userDetails.getUser().getId());
        return responseService.getSuccessResult();
    }
}
