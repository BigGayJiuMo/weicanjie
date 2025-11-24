package com.jiumo.weicanjie.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private UserInfo userInfo;  // 注意这里改为 UserInfo 类型

    @Data
    public static class UserInfo {
        private String nickname;
        private String avatarUrl;
    }
}