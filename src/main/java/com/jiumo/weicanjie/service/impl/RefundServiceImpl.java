package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.entity.RefundRecord;
import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.mapper.RefundRecordMapper;
import com.jiumo.weicanjie.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void createRefund(Long orderId, Long userId, Long restaurantId,
                             String reason, String remark) {

        Order order = orderMapper.selectById(orderId);

        RefundRecord record = new RefundRecord();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setRestaurantId(restaurantId);
        record.setRefundReason(reason);
        record.setRefundRemark(remark);
        record.setStatus(1);  // 申请中

        record.setPreviousStatus(order.getStatus());

        refundRecordMapper.insertRefund(record);
    }

    @Override
    public void approveRefund(Long orderId) {
        refundRecordMapper.updateRefundStatus(orderId, 2); // 2 = 同意退款
    }

    @Override
    public void rejectRefund(Long orderId) {

        RefundRecord record = refundRecordMapper.getByOrderId(orderId);
        if (record == null) {
            throw new RuntimeException("退款记录不存在");
        }

        int previous = record.getPreviousStatus();
        if (previous == 0) {
            previous = 4; // 默认已完成
        }

        refundRecordMapper.updateRefundStatus(orderId, 3);

        orderMapper.updateOrderStatusOnly(orderId, previous);
    }
}
