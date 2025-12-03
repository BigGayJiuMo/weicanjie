package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long restaurantId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private BigDecimal packingFee; // 打包费

    private Integer status; // 1=待支付 2=待处理 3=已完成 4=已取消
    private Integer payStatus; // 0-未支付，1-已支付，2-支付失败，3-已退款
    private String transactionId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    // 非数据库字段
    @TableField(exist = false)
    private List<OrderItem> orderItems;

    @TableField(exist = false)
    private Restaurant restaurant;

    @TableField(exist = false)
    private User user;
}