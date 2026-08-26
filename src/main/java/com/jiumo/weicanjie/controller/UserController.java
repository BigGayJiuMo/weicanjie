package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.BindPhoneRequest;
import com.jiumo.weicanjie.dto.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.service.UserService;
import com.jiumo.weicanjie.service.UserStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashMap;

/**
 * 用户管理控制器
 * 该控制器提供用户信息的管理功能，包括用户登录、注册、个人资料更新、绑定手机号、微信登录及绑定等操作。
 */
@Tag(name = "用户端-用户", description = "用户登录、个人资料、绑定手机号/微信等接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStatsService userStatsService;

    /**
     * 手机号登录
     *
     * 该接口用于通过手机号登录系统。手机号是唯一的登录入口。
     *
     * @param request 包含手机号的登录请求对象
     * @return 返回登录成功后的用户信息和Token等
     */
    @Operation(summary = "手机号登录", description = "通过手机号登录，返回用户信息和 JWT Token")
    @PostMapping("/loginByPhone")
    public Result<HashMap<String, Object>> loginByPhone(@RequestBody @Valid PhoneLoginRequest request) {
        return userService.loginByPhone(request.getPhone());
    }

    /**
     * 获取指定ID的用户信息
     *
     * 该接口用于根据用户ID查询该用户的详细信息。
     *
     * @param id 用户ID
     * @return 返回用户的详细信息，如果用户不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    /**
     * 更新用户信息
     *
     * 该接口用于更新用户的基本信息，如用户名、邮箱等。
     *
     * @param user 包含更新信息的用户对象
     * @return 返回更新后的用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    /**
     * 更新用户个人资料
     *
     * 该接口用于更新用户的个人资料，如昵称和头像。
     *
     * @param request 包含用户ID、昵称和头像URL的请求对象
     * @return 返回更新后的用户信息
     */
    @PostMapping("/updateProfile")
    public Result<User> updateUserProfile(@RequestBody UpdateProfileRequest request) {
        return userService.updateUserProfile(request.getUserId(), request.getNickname(), request.getAvatarUrl());
    }

    /**
     * 绑定手机号
     *
     * 该接口用于绑定用户的手机号。
     *
     * @param request 包含用户ID和手机号的绑定请求对象
     * @return 返回绑定成功或失败的结果
     */
    @PostMapping("/bindPhone")
    public Result<String> bindPhone(@RequestBody BindPhoneRequest request) {
        return userService.bindPhone(request.getUserId(), request.getPhone());
    }

    /**
     * 获取用户统计信息
     *
     * 该接口用于查询指定用户的统计信息，如访问量、订单数等。
     *
     * @param userId 用户ID
     * @return 返回用户的统计信息，如果用户统计不存在则返回错误信息
     */
    @GetMapping("/stats")
    public Result<UserStats> getUserStats(@RequestParam Long userId) {
        UserStats stats = userStatsService.getStats(userId);
        return stats != null ? Result.success(stats) : Result.error("用户统计不存在");
    }

    /**
     * 微信登录
     *
     * 该接口用于通过微信登录系统，用户需要提供微信授权的code以及用户信息。
     *
     * @param request 包含微信授权code和用户信息的请求对象
     * @return 返回登录成功后的用户信息和Token等
     */
    @Operation(summary = "微信登录", description = "通过微信授权 code 登录，返回用户信息和 JWT Token")
    @PostMapping("/loginByWeChat")
    public Result<HashMap<String, Object>> loginByWeChat(@RequestBody @Valid LoginRequest request) {
        return userService.loginByWeChat(request.getCode(), request.getUserInfo());
    }

    /**
     * 绑定微信
     *
     * 该接口用于将用户的微信账号与系统账号进行绑定。
     *
     * @param request 包含用户ID和微信openid的绑定请求对象
     * @return 返回绑定成功或失败的结果
     */
    @PostMapping("/bindWeChat")
    public Result<String> bindWeChat(@RequestBody BindWeChatRequest request) {
        return userService.bindWeChat(request.getUserId(), request.getOpenid());
    }

    @Data
    public static class WeChatLoginRequest {
        private String code;  // 微信授权code
        private LoginRequest.UserInfo userInfo;  // 用户信息
    }

    @Data
    public static class BindWeChatRequest {
        private Long userId;  // 用户ID
        private String openid;  // 微信openid
    }

    @Data
    public static class PhoneLoginRequest {
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
        private String phone;  // 手机号
    }

    @Data
    public static class UpdateProfileRequest {
        private Long userId;  // 用户ID
        private String nickname;  // 用户昵称
        private String avatarUrl;  // 用户头像URL
    }
}
