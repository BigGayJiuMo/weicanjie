package com.jiumo.weicanjie.dto;

import lombok.Data;

/**
 * 登录请求数据传输对象
 * 该类封装了登录时的验证码、用户信息（昵称、头像）和手机号码。
 */
@Data
public class LoginRequest {
    private String code;        // 登录验证码
    private UserInfo userInfo;  // 用户信息对象，包含昵称和头像
    private String phone;       // 用户的手机号码

    @Data
    public static class UserInfo {
        private String nickname;   // 用户昵称
        private String avatarUrl;  // 用户头像URL
    }
}
