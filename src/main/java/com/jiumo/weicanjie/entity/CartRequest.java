package com.jiumo.weicanjie.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartRequest {
    private Long userId;
    private Long restaurantId;
    private Long dishId;
    private Integer quantity;
    private BigDecimal price;
    private String notes;
}