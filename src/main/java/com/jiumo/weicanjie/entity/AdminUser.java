package com.jiumo.weicanjie.entity;

import lombok.Data;

/**
 * 管理端账号实体类，对应 admin_user 表。
 * 用于后台管理员、商家账号登录管理。
 */
@Data
public class AdminUser {

    private Long id;  // 管理员ID
    private String username;  // 登录账号
    private String password;  // 登录密码
    private String role;  // 角色：admin/merchant/kitchen

    private Long restaurantId;  // 商家/后厨绑定餐厅ID（超管为 null）
    private String createdTime;  // 创建时间（字符串格式）
}

