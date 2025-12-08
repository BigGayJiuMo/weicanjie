package com.jiumo.weicanjie.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 购物车请求数据传输对象
 * 该类封装了用户购物车中某个菜品的相关信息，包括用户ID、餐厅ID、菜品ID、数量、价格和备注。
 */
@Data
public class CartRequest {
    private Long userId;       // 用户ID
    private Long restaurantId; // 餐厅ID
    private Long dishId;       // 菜品ID
    private Integer quantity;  // 菜品数量
    private BigDecimal price;  // 菜品单价
    private String notes;      // 备注信息
}
