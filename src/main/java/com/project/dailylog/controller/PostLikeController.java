package com.project.dailylog.controller;

import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.PostLikeService;
import com.project.dailylog.service.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@AllArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final ResponseService responseService;

    @PostMapping("/{postId}")
    public CommonResult likePost(@PathVariable(value = "postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postLikeService.postLike(postId, userDetails.getUser().getId());
        return responseService.getSuccessResult();
    }

    @DeleteMapping("/{postId}")
    public CommonResult deleteLike(@PathVariable(value = "postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postLikeService.deleteLike(postId, userDetails.getUser().getId());
        return responseService.getSuccessResult();
    }
}
