package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 后厨订单管理控制器
 * 该控制器处理后厨查看和更新订单的请求，包括待处理订单、制作中的订单以及订单状态的更新。
 */
@RestController
@RequestMapping("/kitchen/order")
public class KitchenOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取待处理及制作中的订单
     * 根据餐厅ID查询订单，商家和后厨只能查看自己餐厅的订单。
     * @param restaurantId 餐厅ID，前端传递或通过角色获取
     * @param request 请求对象，用于获取当前用户的角色和餐厅ID
     * @return 返回该餐厅的待处理和制作中的订单列表
     */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(required = false) Long restaurantId,
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家和后厨只能查看自己餐厅的订单
        if ("merchant".equals(role) || "kitchen".equals(role)) {
            restaurantId = rid;
        }

        return orderService.getKitchenOrderList(restaurantId);
    }

    /**
     * 接单操作，将订单状态从"待处理"更新为"制作中"
     * 后厨人员接单后，订单进入制作状态
     * @param orderId 订单ID
     * @return 返回接单操作结果
     */
    @PostMapping("/accept/{orderId}")
    public Result<?> accept(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, 3); // 3 代表"制作中"
    }

    /**
     * 完成订单制作，将订单状态从"制作中"更新为"已完成"
     * 后厨人员完成制作后，更新订单状态为已完成
     * @param orderId 订单ID
     * @return 返回完成操作结果
     */
    @PostMapping("/finish/{orderId}")
    public Result<?> finish(@PathVariable Long orderId) {
        return orderService.updateOrderStatus(orderId, 4); // 4 代表"已完成"
    }
}
