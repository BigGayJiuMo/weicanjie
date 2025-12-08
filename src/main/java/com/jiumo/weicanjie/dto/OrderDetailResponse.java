package com.jiumo.weicanjie.dto;

import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.entity.OrderItem;
import com.jiumo.weicanjie.entity.Restaurant;
import lombok.Data;

import java.util.List;

/**
 * 订单详情响应数据传输对象
 * 该类封装了订单详情的返回数据，包括订单本身、餐厅信息、订单项（菜品列表）以及费用信息。
 */
@Data
public class OrderDetailResponse {
    private Order order;            // 订单信息
    private Restaurant restaurant;  // 餐厅信息
    private List<OrderItem> orderItems; // 订单项列表（菜品信息）
    private String subTotal;        // 菜品小计
    private String deliveryFee;     // 配送费用
    private String packingFee;      // 打包费用
    private String totalAmount;     // 总金额
}
