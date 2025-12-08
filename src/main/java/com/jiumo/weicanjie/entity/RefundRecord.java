package com.jiumo.weicanjie.entity;

import lombok.Data;
import java.util.Date;

/**
 * 退款记录实体类，对应 refund_record 表。
 * 用于表示订单退款相关的信息，包括退款原因、退款状态等。
 */
@Data
public class RefundRecord {

    private Long id;  // 退款记录的唯一标识符
    private Long orderId;  // 关联的订单ID
    private Long userId;  // 关联的用户ID
    private Long restaurantId;  // 关联的餐厅ID

    private String refundReason;  // 选择的退款原因
    private String refundRemark;  // 退款的详细说明

    private Integer status;  // 退款状态：1-申请中，2-同意退款，3-拒绝退款
    private Integer previousStatus;  // 订单的原始状态
    private Date applyTime;  // 退款申请时间
    private Date approveTime;  // 退款审批时间

    private Double refundAmount;  // 退款金额

    private Date createdTime;  // 创建时间
    private Date updatedTime;  // 更新时间
}


