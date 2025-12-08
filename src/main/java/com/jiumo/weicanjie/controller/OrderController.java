package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.entity.OrderRequest;
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
    public Result<Order> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request.getOrder(), request.getItems());
    }

    /**
     * 批量创建订单
     */
    @PostMapping("/create/batch")
    public Result<List<Order>> createBatchOrders(@RequestBody BatchOrderRequest request) {
        return orderService.createBatchOrders(request.getRestaurants());
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
     * 获取订单完整详情（包含餐厅、订单项等完整信息）
     */
    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> getOrderFullDetail(@PathVariable Long orderId) {
        return orderService.getOrderFullDetail(orderId);
    }

    /**
     * 模拟微信支付
     */
    @PostMapping("/pay/{orderId}")
    public Result<String> simulateWechatPay(@PathVariable Long orderId) {
        return orderService.simulateWechatPay(orderId);
    }

    /**
     * 取消订单（包括：待支付、待处理）
     * 前端调用：/order/cancel/{orderId}
     */
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 如果你以后真要做“取消支付”这个概念，
     * 可以留一个单独的接口（可选）
     */
    @PostMapping("/cancelPayment/{orderId}")
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

    /**
     * 获取用户完整订单列表（带餐厅 + 菜品）
     */
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getUserOrderList(@PathVariable Long userId) {
        return orderService.getUserOrderList(userId);
    }

    /**
     * 搜索订单（按餐厅/菜品）
     */
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchOrders(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        return orderService.searchOrders(userId, keyword);
    }

    /** 用户申请退款 */
    @PostMapping("/refund/apply")
    public Result<String> applyRefund(@RequestBody RefundApplyRequest req) {
        return orderService.requestRefund(req.getOrderId(), req.getReason(), req.getRemark());
    }

    @PostMapping("/updateRemark")
    public Result<?> updateRemark(@RequestBody Map<String, Object> req) {
        Long orderId = Long.valueOf(req.get("orderId").toString());
        String remark = (String) req.get("remark");

        Order order = orderService.getById(orderId);
        if (order == null) return Result.error("订单不存在");

        order.setRemark(remark);
        orderService.updateById(order);

        return Result.ok();
    }

    @Data
    public static class RefundApplyRequest {
        private Long orderId;
        private String reason;
        private String remark;
    }

    @Data
    public static class BatchOrderRequest {
        private List<SingleOrderRequest> restaurants;

        @Data
        public static class SingleOrderRequest {
            private OrderRequest.OrderDTO order;
            private List<OrderRequest.OrderItemRequest> items;
        }
    }

}