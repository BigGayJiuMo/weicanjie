package com.jiumo.weicanjie.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单请求数据传输对象
 * 该类封装了用户创建订单时传入的订单基本信息以及订单项（菜品信息）。
 */
@Data
public class OrderRequest {
    private OrderDTO order;         // 订单基本信息
    private List<OrderItemRequest> items; // 订单项列表

    @Data
    public static class OrderDTO {
        private Long userId;         // 用户ID
        private Long restaurantId;   // 餐厅ID
        private BigDecimal totalAmount; // 订单总金额
        private BigDecimal packingFee;  // 打包费
        private String remark;          // 订单备注
        private Integer eatType;        // 用餐类型（堂食/外带）
    }

    @Data
    public static class OrderItemRequest {
        private Long dishId;        // 菜品ID
        private String dishName;    // 菜品名称
        private BigDecimal dishPrice; // 菜品单价
        private String dishImageUrl; // 菜品图片URL
        private Integer quantity;   // 菜品数量
        private BigDecimal subtotal; // 小计
    }
}
