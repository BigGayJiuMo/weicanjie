package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.entity.RefundRecord;

public interface RefundService {

    void createRefund(Long orderId, Long userId, Long restaurantId,
                      String reason, String remark);

    void approveRefund(Long orderId);

    void rejectRefund(Long orderId);
}
