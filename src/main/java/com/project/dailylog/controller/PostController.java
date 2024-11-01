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
    public void getPostById(@PathVariable Long postId) {
        // 단건 조회 로직
    }

    @GetMapping("/user/{username}")
    public void getPostsByUser(@PathVariable String username) {
        // 특정 유저 게시물 조회 로직
    }

    @GetMapping("/best")
    public void getBestPosts() {
        // 베스트 게시물 조회 로직
    }

    @GetMapping("/recommended")
    public void getRecommendedPosts() {
        // 추천 게시물 조회 로직
    }

    @GetMapping("/neighbors")
    public void getNeighborPosts() {
        // 이웃 게시물 조회 로직
    }

    @GetMapping
    public void getAllPosts() {
        // 전체 게시물 조회 로직
    }

    @PostMapping
    public CommonResult writePost(@RequestBody PostWriteRequest writeRequest, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        PostDTO postDTO = PostDTO.builder()
                .userId(userDetails.getUser().getId())
                .postTitle(writeRequest.getPostTitle())
                .postContent(writeRequest.getPostContent())
                .postVisible(writeRequest.getPostVisible())
                .build();
        postService.postWrite(postDTO);
        return responseService.getSuccessResult();
    }
}
