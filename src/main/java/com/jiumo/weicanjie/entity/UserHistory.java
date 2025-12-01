package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_history")
public class UserHistory {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private LocalDateTime viewedTime;

    // 非数据库字段：关联餐厅信息
    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String imageUrl;

    @TableField(exist = false)
    private String description;

    @TableField(exist = false)
    private BigDecimal avgRating;

    @TableField(exist = false)
    private Integer monthlySales;

    @TableField(exist = false)
    private BigDecimal minOrderAmount;

    @TableField(exist = false)
    private BigDecimal deliveryFee;

    @TableField(exist = false)
    private String deliveryTime;

}
