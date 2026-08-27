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

    // 微信登录不要求手机号(手机号登录走 PhoneLoginRequest 单独校验)
    private String phone;       // 用户的手机号码(选填,仅手机号登录场景使用)

    private UserInfo userInfo;  // 用户信息对象，包含昵称和头像

    @Data
    public static class UserInfo {
        private String nickname;   // 用户昵称
        private String avatarUrl;  // 用户头像URL
    }
}
