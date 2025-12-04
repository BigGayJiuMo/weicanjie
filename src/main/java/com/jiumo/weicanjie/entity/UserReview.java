package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
@Data
@TableName("user_review")
public class UserReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    private Long restaurantId;

    private Integer rating;

    private Integer taste;

    private Integer pack;

    private String content;

    @TableField("image_urls")
    private String imageUrls;

    // ⭐ 小程序 Boolean -> 后端 Integer（1/0）
    @TableField("is_anonymous")
    private Integer isAnon;
}
