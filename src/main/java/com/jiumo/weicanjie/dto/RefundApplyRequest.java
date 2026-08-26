package com.jiumo.weicanjie.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 退款申请请求数据传输对象
 * 该类封装了用户申请退款时传入的订单ID、退款原因和备注信息。
 */
@Data
public class RefundApplyRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;  // 订单ID

    @NotBlank(message = "退款原因不能为空")
    private String reason; // 退款原因

    @Size(max = 500, message = "退款备注最长500字")
    private String remark; // 退款备注
}
