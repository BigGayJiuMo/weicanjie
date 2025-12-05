package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderAutoCancelTask {

    @Autowired
    private OrderMapper orderMapper;

    // 每 1 分钟执行一次
    @Scheduled(fixedRate = 60000)
    public void autoCancelOrders() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(30);

        log.info("自动取消任务开始：检查 30 分钟未支付订单");

        // 查询超过30分钟仍未支付的订单
        List<Order> overdueOrders = orderMapper.selectOverdueUnpaidOrders(expireTime);

        for (Order order : overdueOrders) {
            // 更新订单状态为 4（取消）
            orderMapper.updateOrderStatus(order.getId(), 4);

            log.info("订单 {} 超过30分钟未支付，已自动取消", order.getOrderNumber());
        }
    }
}
