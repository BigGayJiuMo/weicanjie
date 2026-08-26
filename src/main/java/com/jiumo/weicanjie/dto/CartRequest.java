package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 购物车请求数据传输对象
 * 该类封装了用户购物车中某个菜品的相关信息，包括用户ID、餐厅ID、菜品ID、数量、价格和备注。
 * <p>
 * 注意：quantity 用 @Min 而非 @NotNull，因为 clearCart / removeRestaurantCart
 * 接口只传 userId + restaurantId，不传数量（共用 DTO 时的兼容策略）。
 */
@Data
public class CartRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;       // 用户ID

    @NotNull(message = "餐厅ID不能为空")
    private Long restaurantId; // 餐厅ID

    private Long dishId;       // 菜品ID（更新/移除时必填，业务层校验）

    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;  // 菜品数量

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;  // 菜品单价

    @Size(max = 200, message = "备注最长200字")
    private String notes;      // 备注信息
}
