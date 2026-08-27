package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 餐厅图片实体类，对应 restaurant_images 表。
 * 用于餐厅环境图等。
 */
@Data
@TableName("restaurant_images")
public class RestaurantImage {

    @TableId(type = IdType.AUTO)
    private Long id;  // 图片记录ID

    private Long restaurantId;  // 餐厅ID
    private String imageUrl;  // 图片URL
    private Integer sortOrder;  // 排序序号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 上传时间
}

