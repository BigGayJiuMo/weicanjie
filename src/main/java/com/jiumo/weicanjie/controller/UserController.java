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
     * 微信登录 - 重写为支持手机号注册和登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        System.out.println("收到登录请求，code: " + request.getCode());
        System.out.println("用户信息: " + request.getUserInfo());
        System.out.println("手机号: " + request.getPhone());
        return userService.wechatLogin(request.getCode(), request.getUserInfo(), request.getPhone());
    }

    /**
     * 手机号登录
     */
    @PostMapping("/loginByPhone")
    public Result<User> loginByPhone(@RequestBody PhoneLoginRequest request) {
        System.out.println("手机号登录请求，phone: " + request.getPhone());
        return userService.loginByPhone(request.getPhone());
    }

    /**
     * 注册或登录（整合接口）
     */
    @PostMapping("/registerOrLogin")
    public Result<User> registerOrLogin(@RequestBody RegisterOrLoginRequest request) {
        System.out.println("注册或登录请求，code: " + request.getCode());
        System.out.println("用户信息: " + request.getUserInfo());
        System.out.println("手机号: " + request.getPhone());
        return userService.registerOrLogin(request.getCode(), request.getUserInfo(), request.getPhone());
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

    /**
     * 手机号登录请求体
     */
    @Data
    public static class PhoneLoginRequest {
        private String phone;
    }

    /**
     * 注册或登录请求体
     */
    @Data
    public static class RegisterOrLoginRequest {
        private String code;
        private LoginRequest.UserInfo userInfo;
        private String phone;
    }
}