package com.jiumo.weicanjie.entity;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailResponse {
    private Order order;
    private Restaurant restaurant;
    private List<OrderItem> orderItems;
    private String subTotal;
    private String deliveryFee;
    private String packingFee;
    private String totalAmount;
}