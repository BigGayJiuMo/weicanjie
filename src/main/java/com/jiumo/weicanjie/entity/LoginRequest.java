package com.jiumo.weicanjie.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;
    private UserInfo userInfo;
    private String phone;

    @Data
    public static class UserInfo {
        private String nickname;
        private String avatarUrl;
    }
}