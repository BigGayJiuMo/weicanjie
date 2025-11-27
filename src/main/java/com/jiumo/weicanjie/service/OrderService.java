package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.common.Result;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Result<Order> createOrder(Order order, List<Map<String, Object>> items);

    /**
     * 获取用户订单列表
     */
    Result<List<Order>> getUserOrders(Long userId);

    /**
     * 获取订单详情
     */
    Result<Order> getOrderDetail(Long orderId);

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
     * 根据订单号查询订单
     */
    Result<Order> getOrderByNumber(String orderNumber);
}