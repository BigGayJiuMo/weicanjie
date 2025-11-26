// BusinessHours.java
package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("restaurant_business_hours")
public class BusinessHours {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long restaurantId;
    private Integer dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Integer isOpen;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}