package com.project.dailylog.controller;

import com.project.dailylog.model.entity.UserSubscribe;
import com.project.dailylog.model.response.CommonResult;
import com.project.dailylog.model.response.SubscriptionResponse;
import com.project.dailylog.security.user.CustomUserDetails;
import com.project.dailylog.service.ResponseService;
import com.project.dailylog.service.UserSubscribeService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/subscribe")
@AllArgsConstructor
public class UserSubscribeController {

    private final UserSubscribeService userSubscribeService;
    private final ResponseService responseService;

    @PostMapping("/{subUserId}")
    public CommonResult subscribe(@PathVariable(value = "subUserId") Long subUserId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userSubscribeService.subscribe(userDetails.getUser().getId(), subUserId);
        return responseService.getSuccessResult();
    }

    @DeleteMapping("/{subUserId}")
    public CommonResult unsubscribe(@PathVariable(value = "subUserId") Long subUserId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        userSubscribeService.unsubscribe(userDetails.getUser().getId(), subUserId);
        return responseService.getSuccessResult();
    }

    @GetMapping("/subscriptions")
    public CommonResult getSubscriptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SubscriptionResponse> subscriptions = userSubscribeService.getSubscriptions(userDetails.getUser().getId());
        return responseService.getListResult(subscriptions);
    }

    @GetMapping("/subscribers")
    public CommonResult getSubscribers(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<SubscriptionResponse> subscribers = userSubscribeService.getSubscribers(userDetails.getUser().getId());
        return responseService.getListResult(subscribers);
    }
}
