package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户评价实体类，对应 user_review 表。
 * 用户为餐厅下订单后的评价内容。
 */
@Data
@TableName("user_review")
public class UserReview {

    @TableId(type = IdType.AUTO)
    private Long id;  // 评价ID

    @TableField("user_id")
    private Long userId;  // 用户ID

    @TableField("order_id")
    private Long orderId;  // 订单ID

    @TableField("restaurant_id")
    private Long restaurantId;  // 餐厅ID

    private Integer rating;  // 总体评分
    private Integer taste;  // 口味评分
    private Integer pack;  // 包装评分

    private String content;  // 评价内容

    @TableField("image_urls")
    private String imageUrls;  // 图片（JSON数组字符串）

    @TableField("is_anonymous")
    private Integer isAnonymous;  // 是否匿名：0否 1是

    @TableField("reply_content")
    private String replyContent;  // 商家回复内容

    @TableField("reply_time")
    private Date replyTime;  // 回复时间

    private Integer status;  // 状态：1正常 0屏蔽

    @TableField("review_status")
    private Integer reviewStatus;  // 审核状态：0未审核 1通过 2驳回

    @TableField("reject_reason")
    private String rejectReason;  // 驳回原因

    @TableField("review_time")
    private Date reviewTime;  // 审核时间

    @TableField("created_time")
    private Date createdTime;  // 创建时间

    @TableField("updated_time")
    private Date updatedTime;  // 更新时间
}

