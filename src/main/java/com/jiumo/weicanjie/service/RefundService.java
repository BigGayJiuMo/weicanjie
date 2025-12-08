package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.entity.RefundRecord;

/**
 * 退款服务接口。
 * <p>
 * 该接口定义了与订单退款相关的业务操作，提供退款申请、审批、拒绝等功能。
 * </p>
 */
public interface RefundService {

    /**
     * 创建退款记录。
     * <p>
     * 根据订单信息和退款原因，创建一条新的退款记录，状态默认为“申请中”。
     * </p>
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param reason 退款原因
     * @param remark 退款备注
     */
    void createRefund(Long orderId, Long userId, Long restaurantId,
                      String reason, String remark);

    /**
     * 批准退款请求。
     * <p>
     * 该方法用于将退款请求的状态更新为“已批准”，并处理相关的退款流程。
     * </p>
     *
     * @param orderId 订单ID
     */
    void approveRefund(Long orderId);

    /**
     * 拒绝退款请求。
     * <p>
     * 该方法用于将退款请求的状态更新为“已拒绝”，并记录拒绝原因。
     * </p>
     *
     * @param orderId 订单ID
     */
    void rejectRefund(Long orderId);
}
