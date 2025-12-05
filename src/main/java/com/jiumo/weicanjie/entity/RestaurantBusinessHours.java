package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("restaurant_business_hours")
public class RestaurantBusinessHours {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long restaurantId;

    private Integer dayOfWeek;   // 1～7

    private LocalTime openTime;

    private LocalTime closeTime;

    private Integer isOpen;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
