package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/kitchen/order")
public class KitchenOrderController {

    @Autowired
    private OrderService orderService;

    /** 后厨获取待处理 + 制作中的订单 */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(required = false) Long restaurantId,
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // merchant / kitchen 只能看自己餐厅
        if ("merchant".equals(role) || "kitchen".equals(role)) {
            restaurantId = rid;
        }

        return orderService.getKitchenOrderList(restaurantId);
    }


    /** 后厨接单（状态：待处理 → 制作中） */
    @PostMapping("/accept/{orderId}")
    public Result<?> accept(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, 3);
    }

    /** 后厨完成制作（状态：制作中 → 已完成） */
    @PostMapping("/finish/{orderId}")
    public Result<?> finish(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, 4);
    }
}
