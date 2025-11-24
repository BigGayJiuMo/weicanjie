package com.weicanjie.controller;

import com.weicanjie.common.Result;
import com.weicanjie.entity.LoginRequest;
import com.weicanjie.entity.User;
import com.weicanjie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 绑定手机号
     */
    @PostMapping("/bindPhone")
    public Result<String> bindPhone(@RequestParam Long userId,
                                    @RequestParam String phone) {
        return userService.bindPhone(userId, phone);
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
}