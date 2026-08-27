package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量创建订单请求数据传输对象
 * 该类封装了批量下单时多个餐厅的订单信息。
 * <p>
 * 嵌套校验：restaurants 列表加 @NotEmpty + @Valid 级联校验内部 SingleOrderRequest。
 */
@Data
public class BatchOrderRequest {

    @NotEmpty(message = "批量订单不能为空")
    @Valid
    private List<SingleOrderRequest> restaurants; // 多个餐厅的订单

    @Data
    public static class SingleOrderRequest {
        @Valid
        private OrderRequest.OrderDTO order; // 订单对象

        @Valid
        private List<OrderRequest.OrderItemRequest> items; // 订单项列表
    }
}
