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

    /** 后台分页查询订单 */
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

        // 商家只能看自己餐厅订单
        if ("merchant".equals(role)) {
            restaurantId = rid;
        }

        Page<Order> page = orderService.getAdminOrderPage(pageNum, pageSize, restaurantId, status, keyword);

        return Result.success(page);
    }

    /** 后台订单详情 */
    @GetMapping("/detail/{orderId}")
    public Result<?> detail(@PathVariable Long orderId) {
        return orderService.getOrderFullDetail(orderId);
    }

    /** 修改订单状态（例如：接单、制作完成） */
    @PostMapping("/status/{orderId}")
    public Result<?> updateStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status
    ) {
        return orderService.updateOrderStatus(orderId, status);
    }
}
