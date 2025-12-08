package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户浏览历史实体类，对应 user_history 表。
 * 记录用户浏览过哪些餐厅。
 */
@Data
@TableName("user_history")
public class UserHistory {

    private Long id;  // 记录ID
    private Long userId;  // 用户ID
    private Long restaurantId;  // 餐厅ID

    private LocalDateTime viewedTime;  // 浏览时间

    // 非数据库字段：餐厅关联信息
    @TableField(exist = false)
    private String name;  // 餐厅名称

    @TableField(exist = false)
    private String imageUrl;  // 封面图

    @TableField(exist = false)
    private String description;  // 餐厅描述

    @TableField(exist = false)
    private BigDecimal avgRating;  // 平均评分

    @TableField(exist = false)
    private Integer monthlySales;  // 月销量
}

