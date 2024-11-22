package com.project.dailylog.controller;

import com.project.dailylog.model.dto.PostDTO;
import com.project.dailylog.model.request.PostWriteRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.PostService;
import com.project.dailylog.service.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final ResponseService responseService;

    @GetMapping("/{postId}")
    public CommonResult getPostById(@PathVariable Long postId) throws Exception {
        return responseService.getSingleResult(postService.getPostById(postId));
    }

    @GetMapping("/user/{userEmail}")
    public CommonResult getPostsByUser(@PathVariable String userEmail) throws Exception {
        return responseService.getListResult(postService.getPostsByUser(userEmail));
    }

    @GetMapping("/best/{period}")
    public CommonResult getBestPosts(@PathVariable String period) throws Exception {
        return responseService.getListResult(postService.getBestPosts(period));
    }

    @GetMapping("/recommended")
    public void getRecommendedPosts() throws Exception {
        // 추천 게시물 조회 로직
    }

    @GetMapping("/neighbors")
    public CommonResult getNeighborPosts(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return responseService.getListResult(postService.getNeighborPost(userDetails.getUser().getId()));
    }

    @GetMapping
    public CommonResult getAllPosts() throws Exception {
        return responseService.getListResult(postService.getAllPost());
    }

    @PostMapping
    public CommonResult postWrite(@RequestBody PostWriteRequest writeRequest, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        postService.postWrite(writeRequest, userDetails.getUser());
        return responseService.getSuccessResult();
    }
}
