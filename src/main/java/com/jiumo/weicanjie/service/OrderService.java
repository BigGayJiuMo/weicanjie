package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.controller.OrderController;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.OrderRequest;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Result<Order> createOrder(OrderRequest.OrderDTO order, List<OrderRequest.OrderItemRequest> items);

    /**
     * 批量创建订单
     */
    Result<List<Order>> createBatchOrders(List<OrderController.BatchOrderRequest.SingleOrderRequest> orderRequests);

    /**
     * 获取用户订单列表
     */
    Result<List<Order>> getUserOrders(Long userId);

    /**
     * 获取订单详情
     */
    Result<Order> getOrderDetail(Long orderId);

    /**
     * 获取订单完整详情（包含餐厅、订单项等完整信息）
     */
    Result<Map<String, Object>> getOrderFullDetail(Long orderId);

    /**
     * 更新订单状态
     */
    Result<String> updateOrderStatus(Long orderId, Integer status);

    /**
     * 模拟微信支付
     */
    Result<String> simulateWechatPay(Long orderId);

    /**
     * 取消支付
     */
    Result<String> cancelPayment(Long orderId);

    /**
     * 取消订单
     */
    Result<String> cancelOrder(Long orderId);

    /**
     * 根据订单号查询订单
     */
    Result<Order> getOrderByNumber(String orderNumber);

    /**
     * 获取用户订单列表（含餐厅信息 + 订单项 + 图片等）
     */
    Result<List<Map<String, Object>>> getUserOrderList(Long userId);

    /**
     * 搜索订单（按餐厅/菜品）
     */
    Result<List<Map<String, Object>>> searchOrders(Long userId, String keyword);


}