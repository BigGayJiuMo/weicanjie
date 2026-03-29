package com.jiumo.weicanjie.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishSalesDTO {
    private Long dishId;
    private String dishName;
    private Integer totalSold;
    private BigDecimal totalSales;
    private String restaurantName;
}