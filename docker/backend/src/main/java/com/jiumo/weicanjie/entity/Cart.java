package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类，对应 cart 表。
 * 保存用户将菜品加入购物车的临时数据。
 */
@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;  // 购物车记录ID

    private Long userId;  // 用户ID
    private Long restaurantId;  // 餐厅ID
    private Long dishId;  // 菜品ID

    private Integer quantity;  // 加购数量
    private BigDecimal price;  // 加入购物车时的价格快照
    private String notes;  // 备注信息（如少辣、不要葱）

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;  // 更新时间

    // 非数据库字段：关联菜品
    @TableField(exist = false)
    private Dish dish;

    // 非数据库字段：关联餐厅
    @TableField(exist = false)
    private Restaurant restaurant;
}