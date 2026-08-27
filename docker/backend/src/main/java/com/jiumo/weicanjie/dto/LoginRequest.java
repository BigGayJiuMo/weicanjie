package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 登录请求数据传输对象
 * 该类封装了登录时的验证码、用户信息（昵称、头像）和手机号码。
 */
@Data
public class LoginRequest {
    @NotBlank(message = "验证码不能为空")
    private String code;        // 登录验证码

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;       // 用户的手机号码

    private UserInfo userInfo;  // 用户信息对象，包含昵称和头像

    @Data
    public static class UserInfo {
        private String nickname;   // 用户昵称
        private String avatarUrl;  // 用户头像URL
    }
}
