package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 后台分页查询订单
     * @param pageNum 页码（默认值：1）
     * @param pageSize 每页大小（默认值：10）
     * @param restaurantId 餐厅ID（可选，商家角色时自动填充）
     * @param status 订单状态（可选，传入特定状态来过滤）
     * @param keyword 订单号或其他关键字（可选，用于模糊搜索）
     * @param request HTTP请求，用于获取当前用户的角色信息
     * @return 订单列表的分页结果
     * @note 商家角色只能查看自己餐厅的订单
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家只能查看自己餐厅的订单
        if ("merchant".equals(role)) {
            restaurantId = rid;
        }

        Page<Order> page = orderService.getAdminOrderPage(pageNum, pageSize, restaurantId, status, keyword);

        return Result.success(page);
    }

    /**
     * 后台获取订单详情
     * @param orderId 订单ID
     * @return 订单详细信息
     * @note 提供订单的完整信息，包括订单项和相关餐厅信息
     */
    @GetMapping("/detail/{orderId}")
    public Result<?> detail(@PathVariable Long orderId) {
        return orderService.getOrderFullDetail(orderId);
    }

    /**
     * 修改订单状态（例如：接单、制作完成）
     * @param orderId 订单ID
     * @param status 新的订单状态
     * @return 更新结果
     * @note 可以通过此接口改变订单的状态，如接单、完成制作等
     */
    @PostMapping("/status/{orderId}")
    public Result<?> updateStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status
    ) {
        return orderService.updateOrderStatus(orderId, status);
    }

    /**
     * 后台同意退款
     * @param orderId 订单ID
     * @return 退款批准结果
     * @note 只有审核通过的订单才可以进行退款操作
     */
    @PostMapping("/refund/approve/{orderId}")
    public Result<?> approveRefund(@PathVariable Long orderId) {
        return orderService.approveRefund(orderId);
    }

    /**
     * 后台拒绝退款
     * @param orderId 订单ID
     * @return 拒绝退款结果
     * @note 拒绝退款会标记订单为退款失败状态
     */
    @PostMapping("/refund/reject/{orderId}")
    public Result<?> rejectRefund(@PathVariable Long orderId) {
        return orderService.rejectRefund(orderId);
    }
}
