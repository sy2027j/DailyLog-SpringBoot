package com.project.dailylog.controller;

import com.project.dailylog.model.request.PostWriteRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.PostService;
import com.project.dailylog.service.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final ResponseService responseService;

    @GetMapping("/{postId}")
    public CommonResult getPostById(@PathVariable(value = "postId") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return responseService.getSingleResult(postService.getPostById(postId, userId));
    }

    @GetMapping("/user/{userEmail}")
    public CommonResult getPostsByUser(@PathVariable(value = "userEmail")  String userEmail, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return responseService.getSingleResult(postService.getPostsByUser(userEmail, userDetails != null ? userDetails.getUser() : null));
    }

    @GetMapping("/best/{period}")
    public CommonResult getBestPosts(@PathVariable(value = "period") String period, @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return responseService.getListResult(postService.getBestPosts(period, userId));
    }

    @GetMapping("/neighbors")
    public CommonResult getNeighborPosts(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        return responseService.getListResult(postService.getNeighborPost(userDetails.getUser().getId()));
    }

    @GetMapping
    public CommonResult getAllPosts( @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return responseService.getListResult(postService.getAllPost(userId));
    }

    @PostMapping
    public CommonResult postWrite(
            @RequestPart(value = "postWriteRequest") PostWriteRequest writeRequest,
            @RequestPart(value = "postImages[]", required = false) List<MultipartFile> postImages,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        postService.postWrite(writeRequest, postImages, userDetails.getUser());
        return responseService.getSuccessResult();
    }

    @DeleteMapping("/{postId}")
    public CommonResult postDelete(@PathVariable(value = "postId") Long postId) throws Exception {
        postService.postDelete(postId);
        return responseService.getSuccessResult();
    }
}
