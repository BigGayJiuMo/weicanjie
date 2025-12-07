package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_review")
public class UserReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("order_id")
    private Long orderId;

    @TableField("restaurant_id")
    private Long restaurantId;

    private Integer rating;
    private Integer taste;
    private Integer pack;

    private String content;

    @TableField("image_urls")
    private String imageUrls;

    @TableField("is_anonymous")
    private Integer isAnonymous;

    @TableField("reply_content")
    private String replyContent;

    @TableField("reply_time")
    private Date replyTime;

    private Integer status;

    @TableField("review_status")
    private Integer reviewStatus;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("review_time")
    private Date reviewTime;

    @TableField("created_time")
    private Date createdTime;

    @TableField("updated_time")
    private Date updatedTime;
}
