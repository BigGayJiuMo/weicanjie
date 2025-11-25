package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.BindPhoneRequest;
import com.jiumo.weicanjie.entity.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        System.out.println("收到登录请求，code: " + request.getCode());
        System.out.println("用户信息: " + request.getUserInfo());
        return userService.wechatLogin(request.getCode(), request.getUserInfo());
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    /**
     * 更新用户头像和昵称
     */
    @PostMapping("/updateProfile")
    public Result<User> updateUserProfile(@RequestBody UpdateProfileRequest request) {
        System.out.println("更新用户资料，userId: " + request.getUserId() +
                ", nickname: " + request.getNickname() +
                ", avatarUrl: " + request.getAvatarUrl());
        return userService.updateUserProfile(request.getUserId(), request.getNickname(), request.getAvatarUrl());
    }

    /**
     * 绑定手机号
     */
    @PostMapping("/bindPhone")
    public Result<String> bindPhone(@RequestBody BindPhoneRequest request) {
        System.out.println("绑定手机号请求，userId: " + request.getUserId() + ", phone: " + request.getPhone());
        return userService.bindPhone(request.getUserId(), request.getPhone());
    }

    /**
     * 获取当前用户信息（通过openid）
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam String openid) {
        User user = userService.getUserByOpenid(openid);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats(@RequestParam Long userId) {
        System.out.println("获取用户统计数据，userId: " + userId);

        // 模拟用户统计数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("favoriteCount", 5);
        stats.put("orderCount", 12);
        stats.put("reviewCount", 8);

        return Result.success(stats);
    }

    /**
     * 更新资料请求体
     */
    @Data
    public static class UpdateProfileRequest {
        private Long userId;
        private String nickname;
        private String avatarUrl;
    }
}