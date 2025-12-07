package com.jiumo.weicanjie.entity;

import lombok.Data;
import java.util.Date;

@Data
public class RefundRecord {

    private Long id;
    private Long orderId;
    private Long userId;
    private Long restaurantId;

    private String refundReason;     // 选择的原因
    private String refundRemark;     // 详细说明

    private Integer status;          // 1申请中 2同意退款 3拒绝退款
    private Integer previousStatus; //订单原状态
    private Date applyTime;
    private Date approveTime;

    private Double refundAmount;

    private Date createdTime;
    private Date updatedTime;
}
