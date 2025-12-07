package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.entity.RefundRecord;
import com.jiumo.weicanjie.mapper.RefundRecordMapper;
import com.jiumo.weicanjie.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Override
    public void createRefund(Long orderId, Long userId, Long restaurantId,
                             String reason, String remark) {

        RefundRecord record = new RefundRecord();
        record.setOrderId(orderId);
        record.setUserId(userId);
        record.setRestaurantId(restaurantId);
        record.setRefundReason(reason);
        record.setRefundRemark(remark);
        record.setStatus(1);  // 1 = 申请中

        refundRecordMapper.insertRefund(record);
    }

    @Override
    public void approveRefund(Long orderId) {
        refundRecordMapper.updateRefundStatus(orderId, 2); // 2 = 同意退款
    }

    @Override
    public void rejectRefund(Long orderId) {
        refundRecordMapper.updateRefundStatus(orderId, 3); // 3 = 拒绝退款
    }
}
