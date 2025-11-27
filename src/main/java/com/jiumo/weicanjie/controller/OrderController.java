package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.service.OrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.getOrder(), request.getItems());
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 模拟微信支付
     */
    @PostMapping("/pay/{orderId}")
    public Result<String> simulateWechatPay(@PathVariable Long orderId) {
        return orderService.simulateWechatPay(orderId);
    }

    /**
     * 取消支付
     */
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelPayment(@PathVariable Long orderId) {
        return orderService.cancelPayment(orderId);
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/number/{orderNumber}")
    public Result<Order> getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    @Data
    public static class CreateOrderRequest {
        private Order order;
        private List<Map<String, Object>> items;
    }
}