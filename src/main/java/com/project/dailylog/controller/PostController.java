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
    public void getPostById(@PathVariable Long postId) throws Exception {
        // 단건 조회 로직
    }

    @GetMapping("/user/{userEmail}")
    public CommonResult getPostsByUser(@PathVariable String userEmail) throws Exception {
        return responseService.getListResult(postService.getPostsByUser(userEmail));
    }

    @GetMapping("/best")
    public void getBestPosts() throws Exception {
        // 베스트 게시물 조회 로직
    }

    @GetMapping("/recommended")
    public void getRecommendedPosts() throws Exception {
        // 추천 게시물 조회 로직
    }

    @GetMapping("/neighbors")
    public void getNeighborPosts() throws Exception {
        // 이웃 게시물 조회 로직
    }

    @GetMapping
    public CommonResult getAllPosts() throws Exception {
        return responseService.getListResult(postService.getAllPost());
    }

    @PostMapping
    public CommonResult postWrite(@RequestBody PostWriteRequest writeRequest, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
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
