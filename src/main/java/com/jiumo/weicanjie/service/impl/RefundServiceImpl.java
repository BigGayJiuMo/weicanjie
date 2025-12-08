package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.entity.RefundRecord;
import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.mapper.RefundRecordMapper;
import com.jiumo.weicanjie.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 退款服务实现类。
 * <p>
 * 该实现类处理与订单退款相关的业务逻辑，负责创建退款记录、批准退款和拒绝退款操作。
 * </p>
 */
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 创建退款记录。
     * <p>
     * 该方法用于创建一条退款记录，并将退款状态设置为“申请中”。如果订单存在，记录相关的退款信息。
     * </p>
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param reason 退款原因
     * @param remark 退款备注
     */
    @Override
    public void createRefund(Long orderId, Long userId, Long restaurantId,
                             String reason, String remark) {

        // 查询订单信息
        Order order = orderMapper.selectById(orderId);

        // 创建退款记录
        RefundRecord record = new RefundRecord();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setRestaurantId(restaurantId);
        record.setRefundReason(reason);
        record.setRefundRemark(remark);
        record.setStatus(1);  // 状态设置为“申请中”

        // 记录订单的原始状态
        record.setPreviousStatus(order.getStatus());

        // 插入退款记录
        refundRecordMapper.insertRefund(record);
    }

    /**
     * 批准退款请求。
     * <p>
     * 该方法用于将退款记录的状态更新为“同意退款”，表示退款请求已被批准。
     * </p>
     *
     * @param orderId 订单ID
     */
    @Override
    public void approveRefund(Long orderId) {
        refundRecordMapper.updateRefundStatus(orderId, 2); // 2 = 同意退款
    }

    /**
     * 拒绝退款请求。
     * <p>
     * 该方法用于将退款请求的状态更新为“拒绝退款”，并将订单恢复为原始状态。
     * </p>
     *
     * @param orderId 订单ID
     * @throws RuntimeException 如果未找到退款记录，抛出异常
     */
    @Override
    public void rejectRefund(Long orderId) {

        // 查询退款记录
        RefundRecord record = refundRecordMapper.getByOrderId(orderId);
        if (record == null) {
            throw new RuntimeException("退款记录不存在");
        }

        // 获取退款记录中的订单原始状态
        int previous = record.getPreviousStatus();
        if (previous == 0) {
            previous = 4; // 默认恢复为已完成状态
        }

        // 更新退款状态为“拒绝退款”
        refundRecordMapper.updateRefundStatus(orderId, 3);

        // 恢复订单原始状态
        orderMapper.updateOrderStatusOnly(orderId, previous);
    }
}
