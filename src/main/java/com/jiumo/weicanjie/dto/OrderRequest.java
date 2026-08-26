package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单请求数据传输对象
 * 该类封装了用户创建订单时传入的订单基本信息以及订单项（菜品信息）。
 * <p>
 * 嵌套校验说明：order 和 items 是对象/列表，内部字段要校验，
 * 必须在外层字段上加 {@link Valid} 才会级联校验到内部字段（否则注解不生效）。
 */
@Data
public class OrderRequest {

    /** 订单基本信息 */
    @NotNull(message = "订单信息不能为空")
    @Valid
    private OrderDTO order;

    /** 订单项列表 */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderDTO {
        @NotNull(message = "用户ID不能为空")
        private Long userId;             // 用户ID

        @NotNull(message = "餐厅ID不能为空")
        private Long restaurantId;       // 餐厅ID

        @NotNull(message = "订单总金额不能为空")
        @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
        private BigDecimal totalAmount;  // 订单总金额

        @DecimalMin(value = "0", message = "打包费不能为负数")
        private BigDecimal packingFee;   // 打包费

        @Size(max = 500, message = "订单备注最长500字")
        private String remark;           // 订单备注

        @NotNull(message = "用餐类型不能为空")
        private Integer eatType;         // 用餐类型（1堂食/2外带）
    }

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;             // 菜品ID

        @NotBlank(message = "菜品名称不能为空")
        private String dishName;         // 菜品名称

        @NotNull(message = "菜品单价不能为空")
        @DecimalMin(value = "0.01", message = "菜品单价必须大于0")
        private BigDecimal dishPrice;    // 菜品单价

        @Size(max = 500, message = "图片地址过长")
        private String dishImageUrl;     // 菜品图片URL

        @NotNull(message = "菜品数量不能为空")
        @Min(value = 1, message = "菜品数量至少为1")
        private Integer quantity;        // 菜品数量

        @NotNull(message = "小计不能为空")
        @DecimalMin(value = "0", message = "小计不能为负数")
        private BigDecimal subtotal;     // 小计
    }
}
