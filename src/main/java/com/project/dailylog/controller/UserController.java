package com.project.dailylog.controller;

import com.project.dailylog.model.request.UserRequest;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.GcsService;
import com.project.dailylog.service.ResponseService;
import com.project.dailylog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GcsService gcsService;
    private final ResponseService responseService;

    @PostMapping
    public CommonResult updateProfile(
            @RequestPart(value = "userRequest") UserRequest userRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImageUrl = gcsService.upload(profileImage);
            }
            userService.updateUserProfile(userRequest, profileImageUrl, userDetails);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            return responseService.getFailResult("프로필 업데이트 실패: " + e.getMessage());
        }
    }

    @GetMapping
    public CommonResult getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return responseService.getSingleResult(userService.getUserProfile(userDetails.getUser()));
    }

    @DeleteMapping
    public CommonResult deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUserAccount(userDetails.getUser());
        return responseService.getSuccessResult();
    }

    @GetMapping("/social")
    public CommonResult getSocialAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return responseService.getListResult(userService.getUserSocialAccount(userDetails.getUser()));
    }

    @DeleteMapping("/social")
    public CommonResult deleteSocialAccount(@RequestParam String socialAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUserSocialAccount(socialAccountId, userDetails.getUser());
        return responseService.getSuccessResult();
    }
}
