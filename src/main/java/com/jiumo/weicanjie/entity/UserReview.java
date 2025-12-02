package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_review")
public class UserReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    private Long restaurantId;

    /**
     * 总体评分 1-5
     */
    private Integer rating;

    /**
     * 味道评分
     */
    private Integer taste;

    /**
     * 包装评分
     */
    private Integer pack;

    /**
     * 评价文本内容
     */
    private String content;

    /**
     * 图片 JSON 数组（字符串存 JSON）
     */
    private String imageUrls;

    /**
     * 是否匿名 0否 1是
     */
    private Integer isAnon;
}