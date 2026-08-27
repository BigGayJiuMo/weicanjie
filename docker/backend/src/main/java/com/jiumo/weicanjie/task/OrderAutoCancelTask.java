package com.jiumo.weicanjie.task;

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

    /**
     * 自动取消未支付的订单任务
     * 每 1 分钟执行一次，检查是否有未支付且超过 30 分钟的订单，若有则自动取消。
     */
    @Scheduled(fixedRate = 60000)  // 每 1 分钟执行一次
    public void autoCancelOrders() {
        // 设置 30 分钟之前的时间为过期时间
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(30);

        log.info("自动取消任务开始：检查 30 分钟未支付订单");

        // 查询超过 30 分钟未支付的订单
        List<Order> overdueOrders = orderMapper.selectOverdueUnpaidOrders(expireTime);

        // 遍历每个超时未支付的订单，更新其状态为已取消
        for (Order order : overdueOrders) {
            // 更新订单状态为 5（已取消）
            orderMapper.updateOrderStatusOnly(order.getId(), 5);

            // 打印日志，记录已取消的订单
            log.info("订单 {} 超过 30 分钟未支付，已自动取消", order.getOrderNumber());
        }
    }
}
