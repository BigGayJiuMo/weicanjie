package com.jiumo.weicanjie.dto;

import lombok.Data;

/**
 * 管理员登录请求数据传输对象
 * 该类封装了管理员登录时的用户名和密码信息。
 */
@Data
public class AdminLoginDTO {
    private String username;  // 管理员用户名
    private String password;  // 管理员密码

    public AdminLoginDTO() {}
}
