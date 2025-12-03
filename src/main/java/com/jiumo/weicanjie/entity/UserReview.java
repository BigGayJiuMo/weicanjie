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

    @TableField("image_urls") // ⭐映射 JSON 字段
    private String imageUrls;

    @TableField("is_anonymous") // ⭐数据库字段是 is_anonymous
    private Integer isAnon;
}
