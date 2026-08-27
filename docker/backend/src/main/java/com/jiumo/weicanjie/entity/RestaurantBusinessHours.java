package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 餐厅营业时间实体类，对应 restaurant_business_hours 表。
 * 用于表示餐厅每日的营业时间，包括每天的开门与关门时间。
 */
@Data
@TableName("restaurant_business_hours")
public class RestaurantBusinessHours {

    @TableId(type = IdType.AUTO)
    private Long id;  // 唯一标识符

    private Long restaurantId;  // 餐厅ID
    private Integer dayOfWeek;  // 星期几（1-7）
    private LocalTime openTime = LocalTime.of(9, 0);  // 开门时间（默认为9:00）
    private LocalTime closeTime = LocalTime.of(21, 0);  // 关门时间（默认为21:00）
    private Integer isOpen = 0;  // 是否开放：0-关闭，1-开启

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间
}
