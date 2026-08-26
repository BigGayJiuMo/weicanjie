package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.BatchOrderRequest;
import com.jiumo.weicanjie.dto.OrderRequest;
import com.jiumo.weicanjie.dto.RefundApplyRequest;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 订单管理控制器
 * 该控制器提供订单的创建、查询、支付、取消等功能。
 */
@Tag(name = "用户端-订单", description = "下单、支付、取消、退款、搜索等接口")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建单个订单
     *
     * 该接口用于创建一个新的订单，包含订单本身和相关的订单项（如菜品和数量）。
     *
     * @param request 订单请求对象，包含订单和订单项的信息
     * @return 返回创建的订单信息
     */
    @Operation(summary = "创建单个订单", description = "包含订单基本信息与订单项（菜品）列表")
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody @Valid OrderRequest request) {
        return orderService.createOrder(request.getOrder(), request.getItems());
    }

    /**
     * 批量创建订单
     *
     * 该接口用于批量创建多个订单，通常用于商家同时处理多个餐厅的订单。
     *
     * @param request 批量订单请求对象，包含多个餐厅的订单信息
     * @return 返回批量创建的订单列表
     */
    @Operation(summary = "批量创建订单", description = "同时为多个餐厅创建订单")
    @PostMapping("/create/batch")
    public Result<List<Order>> createBatchOrders(@RequestBody @Valid BatchOrderRequest request) {
        return orderService.createBatchOrders(request.getRestaurants());
    }

    /**
     * 获取用户的所有订单列表
     *
     * 该接口用于根据用户ID查询该用户的所有订单。
     *
     * @param userId 用户ID
     * @return 返回用户的所有订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    /**
     * 获取指定订单的详细信息
     *
     * 该接口用于根据订单ID查询订单的详细信息。
     *
     * @param orderId 订单ID
     * @return 返回指定订单的详细信息
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    /**
     * 获取订单的完整详情，包括餐厅和订单项等完整信息
     *
     * 该接口用于查询订单的详细信息，包括餐厅、菜品等所有相关信息。
     *
     * @param orderId 订单ID
     * @return 返回订单的完整详情信息
     */
    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> getOrderFullDetail(@PathVariable Long orderId) {
        return orderService.getOrderFullDetail(orderId);
    }

    /**
     * 模拟微信支付接口
     *
     * 该接口用于模拟微信支付过程（可用于测试阶段），实际支付逻辑由微信支付SDK处理。
     *
     * @param orderId 订单ID
     * @return 返回支付操作的结果
     */
    @PostMapping("/pay/{orderId}")
    public Result<String> simulateWechatPay(@PathVariable Long orderId) {
        return orderService.simulateWechatPay(orderId);
    }

    /**
     * 取消订单
     *
     * 该接口用于取消未支付或未处理的订单。
     *
     * @param orderId 订单ID
     * @return 返回取消操作的结果
     */
    @PostMapping("/cancel/{orderId}")
    public Result<String> cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 取消支付（可选接口）
     *
     * 该接口用于取消已经支付的订单的支付状态。此接口目前为可选功能，实际使用时可根据需求决定是否启用。
     *
     * @param orderId 订单ID
     * @return 返回取消支付操作的结果
     */
    @PostMapping("/cancelPayment/{orderId}")
    public Result<String> cancelPayment(@PathVariable Long orderId) {
        return orderService.cancelPayment(orderId);
    }

    /**
     * 根据订单号查询订单
     *
     * 该接口用于根据订单号查询订单的详细信息。
     *
     * @param orderNumber 订单号
     * @return 返回订单信息
     */
    @GetMapping("/number/{orderNumber}")
    public Result<Order> getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    /**
     * 获取用户的完整订单列表（包括餐厅和菜品信息）
     *
     * 该接口用于获取用户的所有订单，并返回每个订单对应的餐厅和菜品信息。
     *
     * @param userId 用户ID
     * @return 返回用户的完整订单列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<Map<String, Object>>> getUserOrderList(@PathVariable Long userId) {
        return orderService.getUserOrderList(userId);
    }

    /**
     * 搜索订单（按餐厅/菜品关键词）
     *
     * 该接口用于根据用户ID和搜索关键词（餐厅名或菜品名）查询订单。
     *
     * @param userId 用户ID
     * @param keyword 搜索关键词（餐厅名或菜品名）
     * @return 返回符合条件的订单列表
     */
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchOrders(
            @RequestParam Long userId,
            @RequestParam String keyword) {
        return orderService.searchOrders(userId, keyword);
    }

    /**
     * 用户申请退款
     *
     * 该接口用于用户对订单申请退款，并附带退款原因和备注信息。
     *
     * @param req 退款申请请求对象，包含订单ID、退款原因和备注
     * @return 返回退款申请的处理结果
     */
    @PostMapping("/refund/apply")
    public Result<String> applyRefund(@RequestBody RefundApplyRequest req) {
        return orderService.requestRefund(req.getOrderId(), req.getReason(), req.getRemark());
    }

    /**
     * 更新订单备注
     *
     * 该接口用于更新订单的备注信息，供商家或管理员使用。
     *
     * @param req 请求对象，包含订单ID和备注信息
     * @return 返回更新结果
     */
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

    @PostMapping("/confirmPickup/{orderId}")
    public Result<String> confirmPickup(@PathVariable Long orderId) {
        return orderService.confirmPickup(orderId);
    }
}
