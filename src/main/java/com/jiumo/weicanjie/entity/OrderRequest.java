package com.jiumo.weicanjie.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private OrderDTO order;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderDTO {
        private Long userId;
        private Long restaurantId;
        private BigDecimal totalAmount;
        private BigDecimal packingFee;
        private BigDecimal deliveryFee;
    }

    @Data
    public static class OrderItemRequest {
        private Long dishId;
        private String dishName;
        private BigDecimal dishPrice;
        private String dishImageUrl;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}