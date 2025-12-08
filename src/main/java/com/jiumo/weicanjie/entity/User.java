package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序用户实体类，对应 users 表。
 * 保存用户基础资料。
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;  // 用户ID

    private String openid;  // 微信 OpenID
    private String nickname;  // 昵称
    private String avatarUrl;  // 头像
    private String phone;  // 手机号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 注册时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;  // 更新时间
}
