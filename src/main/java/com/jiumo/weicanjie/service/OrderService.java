package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.controller.OrderController;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.OrderRequest;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口。
 * <p>
 * 该接口提供与订单相关的所有业务操作，包括创建订单、查询订单、更新订单状态、处理退款等功能。
 * </p>
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单。
     * <p>
     * 该方法用于创建一个新的订单，包含多个订单项。订单状态将设置为“待支付”。
     * </p>
     *
     * @param order 包含订单信息的订单DTO
     * @param items 订单项列表
     * @return 返回创建的订单
     */
    Result<Order> createOrder(OrderRequest.OrderDTO order, List<OrderRequest.OrderItemRequest> items);

    /**
     * 批量创建订单。
     * <p>
     * 该方法允许批量创建多个订单，并返回这些订单的列表。
     * </p>
     *
     * @param orderRequests 包含多个订单请求的列表
     * @return 返回创建的订单列表
     */
    Result<List<Order>> createBatchOrders(List<OrderController.BatchOrderRequest.SingleOrderRequest> orderRequests);

    /**
     * 获取用户的所有订单。
     * <p>
     * 该方法根据用户ID获取该用户的所有订单。
     * </p>
     *
     * @param userId 用户ID
     * @return 返回用户的所有订单
     */
    Result<List<Order>> getUserOrders(Long userId);

    /**
     * 获取订单详情。
     * <p>
     * 该方法根据订单ID获取订单的详细信息。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回订单的详细信息
     */
    Result<Order> getOrderDetail(Long orderId);

    /**
     * 获取订单的完整详情。
     * <p>
     * 该方法返回订单的所有详细信息，包括餐厅信息、订单项等。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回订单的完整信息
     */
    Result<Map<String, Object>> getOrderFullDetail(Long orderId);

    /**
     * 更新订单状态。
     * <p>
     * 该方法根据订单ID更新订单的状态。
     * </p>
     *
     * @param orderId 订单ID
     * @param status 新的订单状态
     * @return 返回操作结果
     */
    Result<String> updateOrderStatus(Long orderId, Integer status);

    /**
     * 模拟微信支付。
     * <p>
     * 该方法用于模拟微信支付并返回支付结果。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回支付结果
     */
    Result<String> simulateWechatPay(Long orderId);

    /**
     * 取消支付。
     * <p>
     * 该方法用于取消支付并将订单状态更新为“待支付”。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回取消支付的结果
     */
    Result<String> cancelPayment(Long orderId);

    /**
     * 取消订单。
     * <p>
     * 该方法用于取消订单并将订单状态更新为“已取消”。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回取消订单的结果
     */
    Result<String> cancelOrder(Long orderId);

    /**
     * 根据订单号查询订单。
     * <p>
     * 该方法根据订单号查询并返回对应的订单信息。
     * </p>
     *
     * @param orderNumber 订单号
     * @return 返回对应的订单
     */
    Result<Order> getOrderByNumber(String orderNumber);

    /**
     * 获取用户的订单列表（含餐厅信息 + 订单项 + 图片等）。
     * <p>
     * 该方法返回用户的订单列表，包含餐厅信息、订单项以及其他相关信息。
     * </p>
     *
     * @param userId 用户ID
     * @return 返回用户的订单列表
     */
    Result<List<Map<String, Object>>> getUserOrderList(Long userId);

    /**
     * 搜索订单。
     * <p>
     * 该方法根据关键词搜索用户的订单，支持按餐厅或菜品进行搜索。
     * </p>
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return 返回符合条件的订单列表
     */
    Result<List<Map<String, Object>>> searchOrders(Long userId, String keyword);

    /**
     * 后台分页查看订单。
     * <p>
     * 该方法用于后台分页查询订单列表，支持按餐厅、订单状态及关键词筛选。
     * </p>
     *
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param restaurantId 餐厅ID
     * @param status 订单状态
     * @param keyword 搜索关键词
     * @return 返回分页后的订单列表
     */
    Page<Order> getAdminOrderPage(int pageNum, int pageSize, Long restaurantId, Integer status, String keyword);

    /**
     * 后台查看厨房订单。
     * <p>
     * 该方法用于厨房查看与其相关的订单信息。
     * </p>
     *
     * @param restaurantId 餐厅ID
     * @return 返回厨房订单列表
     */
    Result<?> getKitchenOrderList(Long restaurantId);

    /**
     * 用户申请退款。
     * <p>
     * 该方法用于用户发起退款请求，将订单状态更新为“退款中”。
     * </p>
     *
     * @param orderId 订单ID
     * @param reason 退款原因
     * @param remark 退款备注
     * @return 返回申请退款的结果
     */
    Result<String> requestRefund(Long orderId, String reason, String remark);

    /**
     * 后台审核退款请求。
     * <p>
     * 该方法用于后台审核退款请求，并将订单状态更新为“已退款”。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回审核结果
     */
    Result<String> approveRefund(Long orderId);

    /**
     * 后台拒绝退款请求。
     * <p>
     * 该方法用于后台拒绝退款请求，将订单恢复为“已完成”状态。
     * </p>
     *
     * @param orderId 订单ID
     * @return 返回拒绝退款的结果
     */
    Result<String> rejectRefund(Long orderId);

    Result<String> confirmPickup(Long orderId);
}
