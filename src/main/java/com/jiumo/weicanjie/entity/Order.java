package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类，对应 orders 表。
 * 用于表示一个用户的订单信息，包括订单基本信息、支付状态、订单状态等。
 */
@Data
@TableName("orders")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;  // 订单ID

    private Long userId;  // 用户ID
    private Long restaurantId;  // 餐厅ID
    private String orderNumber;  // 订单号
    private BigDecimal totalAmount;  // 总金额
    private BigDecimal packingFee;  // 打包费
    private String remark;  // 订单备注
    private Integer status;  // 订单状态：1=待支付，2=待处理，3=制作中，4=待取餐，5=已取消，6=已完成，7=退款中，8=已退款
    private Integer payStatus;  // 支付状态：0=未支付，1=已支付，2=支付失败，3=已退款
    private String transactionId;  // 交易ID
    private Integer eatType;  // 就餐类型：1=堂食，2=外带

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;  // 支付时间

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;  // 更新时间

    // 非数据库字段
    @TableField(exist = false)
    private List<OrderItem> orderItems;  // 订单项列表

    @TableField(exist = false)
    private Restaurant restaurant;  // 关联餐厅信息

    @TableField(exist = false)
    private User user;  // 关联用户信息
}

