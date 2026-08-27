package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端账号实体类，对应 admin_user 表。
 * 用于后台管理员、商家账号登录管理。
 */
@Data
public class AdminUser {

    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID

    private String username;        // 管理员账号
    private String password;        // BCrypt 密码

    private Integer status;         // 1正常 0禁用
    private String role;            // super / merchant / kitchen

    private Long restaurantId;      // 商家绑定餐厅ID(只有 merchant/kitchen 用)
    private String phone;           // 手机号，可空

    private LocalDateTime createdTime;       // 创建时间
    private LocalDateTime lastLoginTime;     // 最后登录时间
}

