package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.BindPhoneRequest;
import com.jiumo.weicanjie.dto.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.service.UserService;
import com.jiumo.weicanjie.service.UserStatsService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStatsService userStatsService;


    /**
     * 手机号登录（唯一入口）
     */
    @PostMapping("/loginByPhone")
    public Result<User> loginByPhone(@RequestBody PhoneLoginRequest request) {
        return userService.loginByPhone(request.getPhone());
    }


    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }


    @PutMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }


    @PostMapping("/updateProfile")
    public Result<User> updateUserProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateUserProfile(request.getUserId(), request.getNickname(), request.getAvatarUrl());
    }


    @PostMapping("/bindPhone")
    public Result<String> bindPhone(@RequestBody BindPhoneRequest request) {
        return userService.bindPhone(request.getUserId(), request.getPhone());
    }


    @GetMapping("/stats")
    public Result<UserStats> getUserStats(@RequestParam Long userId) {
        UserStats stats = userStatsService.getStats(userId);
        return stats != null ? Result.success(stats) : Result.error("用户统计不存在");
    }


    /**
     * 微信登录
     */
    @PostMapping("/loginByWeChat")
    public Result<User> loginByWeChat(@RequestBody LoginRequest request) {

        return userService.loginByWeChat(
                request.getCode(),
                request.getUserInfo()
        );
    }

    /**
     * 绑定微信
     */
    @PostMapping("/bindWeChat")
    public Result<String> bindWeChat(@RequestBody BindWeChatRequest request) {
        return userService.bindWeChat(request.getUserId(), request.getOpenid());
    }

    @Data
    public static class WeChatLoginRequest {
        private String code;
        private LoginRequest.UserInfo userInfo;
    }

    @Data
    public static class BindWeChatRequest {
        private Long userId;
        private String openid;
    }


    @Data
    public static class PhoneLoginRequest {
        private String phone;
    }

    @Data
    public static class UpdateProfileRequest {
        private Long userId;
        private String nickname;
        private String avatarUrl;
    }
}
