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

    private LocalTime openTime = LocalTime.of(9, 0);

    private LocalTime closeTime = LocalTime.of(21, 0);

    private Integer isOpen = 0;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}
