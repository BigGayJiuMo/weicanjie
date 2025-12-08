package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体类，对应 order_items 表。
 * 用于表示每个订单中的菜品信息，包括菜品名称、价格、数量等。
 */
@Data
@TableName("order_items")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;  // 订单项ID

    private Long orderId;  // 关联订单ID
    private Long dishId;  // 关联菜品ID
    private String dishName;  // 菜品名称
    private BigDecimal dishPrice;  // 菜品价格
    private Integer quantity;  // 菜品数量
    private BigDecimal subtotal;  // 小计

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    // 非数据库字段
    @TableField(exist = false)
    private Dish dish;  // 关联菜品信息

    @TableField(exist = false)
    private String dishImageUrl;  // 菜品图片URL

    public String getDishImageUrl() {
        return dishImageUrl;
    }
}
