package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.controller.OrderController;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.OrderItemMapper;
import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.mapper.UserStatsMapper;
import com.jiumo.weicanjie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserStatsService userStatsService;

    @Autowired
    private RefundService refundService;

    /** 单餐厅创建订单 */
    @Override
    @Transactional
    public Result<Order> createOrder(OrderRequest.OrderDTO orderDTO, List<OrderRequest.OrderItemRequest> items) {
        try {
            // 生成订单号
            String orderNumber = "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);

            Order order = new Order();
            BeanUtils.copyProperties(orderDTO, order);
            order.setOrderNumber(orderNumber);
            order.setStatus(1);         // 待支付
            order.setPayStatus(0);      // 未支付
            order.setCreatedTime(LocalDateTime.now());
            order.setUpdatedTime(LocalDateTime.now());

            // 打包费
            order.setPackingFee(orderDTO.getPackingFee() != null ? orderDTO.getPackingFee() : BigDecimal.ZERO);

            // 总金额（如果前端没传 totalAmount，则服务器自动计算）
            if (orderDTO.getTotalAmount() == null) {
                BigDecimal itemsTotal = items.stream()
                        .map(i -> i.getDishPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal total = itemsTotal.add(order.getPackingFee());
                order.setTotalAmount(total);
            } else {
                order.setTotalAmount(orderDTO.getTotalAmount());
            }

            orderMapper.insert(order);

            // 保存订单项
            List<OrderItem> orderItems = items.stream().map(item -> {
                OrderItem oi = new OrderItem();
                BeanUtils.copyProperties(item, oi);
                oi.setOrderId(order.getId());
                oi.setSubtotal(item.getDishPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                oi.setCreatedTime(LocalDateTime.now());
                return oi;
            }).collect(Collectors.toList());

            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }

            /** -------------------------------
             *  精准删除购物车（只删下单的菜品）
             * -------------------------------*/
            for (OrderRequest.OrderItemRequest item : items) {
                cartService.removeFromCart(order.getUserId(), order.getRestaurantId(), item.getDishId());
            }

            /** -------------------------------
             *  更新用户统计信息
             * -------------------------------*/
            UserStats stats = userStatsService.getOne(
                    new LambdaQueryWrapper<UserStats>().eq(UserStats::getUserId, order.getUserId())
            );

            if (stats == null) {
                stats = new UserStats();
                stats.setUserId(order.getUserId());
                stats.setOrderCount(1);
                stats.setFavoriteCount(0);
                stats.setReviewCount(0);
                stats.setTotalSpent(order.getTotalAmount());
                userStatsService.save(stats);
            } else {
                stats.setOrderCount(stats.getOrderCount() + 1);
                stats.setTotalSpent(stats.getTotalSpent().add(order.getTotalAmount()));
                userStatsService.updateById(stats);
            }

            return Result.success(order);

        } catch (Exception e) {
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }


    /** 多餐厅批量创建订单 */
    @Override
    @Transactional
    public Result<List<Order>> createBatchOrders(List<OrderController.BatchOrderRequest.SingleOrderRequest> list) {
        try {
            List<Order> orders = new ArrayList<>();

            for (OrderController.BatchOrderRequest.SingleOrderRequest req : list) {
                OrderRequest.OrderDTO orderDTO = req.getOrder();
                List<OrderRequest.OrderItemRequest> items = req.getItems();

                Result<Order> result = createOrder(orderDTO, items);

                if (result.getCode() != 200) {
                    throw new RuntimeException("创建订单失败: " + result.getMessage());
                }

                orders.add(result.getData());

                /** 精准删除购物车指定菜品 */
                for (OrderRequest.OrderItemRequest item : items) {
                    cartService.removeFromCart(orderDTO.getUserId(), orderDTO.getRestaurantId(), item.getDishId());
                }
            }

            return Result.success(orders);

        } catch (Exception e) {
            return Result.error("批量创建订单失败: " + e.getMessage());
        }
    }


    @Override
    public Result<List<Order>> getUserOrders(Long userId) {
        try {
            List<Order> orders = orderMapper.selectByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取用户订单失败: " + e.getMessage());
        }
    }


    @Override
    public Result<Order> getOrderDetail(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) return Result.error("订单不存在");
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }


    /** 获取订单完整详情（含菜品图片） */
    @Override
    public Result<Map<String, Object>> getOrderFullDetail(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) return Result.error("订单不存在");

            Restaurant restaurant = restaurantService.getById(order.getRestaurantId());
            List<OrderItem> orderItems = orderItemMapper.selectByOrderIdWithDishInfo(orderId);

            BigDecimal subTotal = orderItems.stream()
                    .map(i -> i.getDishPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> map = new HashMap<>();
            map.put("order", order);
            map.put("restaurant", restaurant);
            map.put("orderItems", orderItems);
            map.put("subTotal", subTotal);
            map.put("packingFee", order.getPackingFee());
            map.put("totalAmount", order.getTotalAmount());
            map.put("remark", order.getRemark());
            return Result.success(map);

        } catch (Exception e) {
            return Result.error("获取订单完整详情失败: " + e.getMessage());
        }
    }


    @Override
    public Result<String> updateOrderStatus(Long orderId, Integer status) {
        try {
            int result = orderMapper.updateOrderStatusOnly(orderId, status);
            return result > 0 ? Result.success("订单状态更新成功")
                    : Result.error("订单状态更新失败");
        } catch (Exception e) {
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }


    /** 模拟支付 */
    @Override
    @Transactional
    public Result<String> simulateWechatPay(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) return Result.error("订单不存在");
            if (order.getStatus() != 1) return Result.error("订单状态异常，无法支付");

            String transactionId = "WX" + System.currentTimeMillis();

            int result = orderMapper.updateOrderPaymentStatus(
                    orderId,
                    2,       // 待处理
                    1,       // 已支付
                    transactionId
            );

            return result > 0 ? Result.success("支付成功")
                    : Result.error("支付失败");

        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }


    /** 取消支付 */
    @Override
    @Transactional
    public Result<String> cancelPayment(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) return Result.error("订单不存在");
            if (order.getStatus() != 1) return Result.error("订单状态无法取消支付");

            int result = orderMapper.updateOrderStatusOnly(orderId, 5);
            return result > 0 ? Result.success("支付已取消")
                    : Result.error("取消支付失败");

        } catch (Exception e) {
            return Result.error("取消支付失败: " + e.getMessage());
        }
    }


    /** 取消订单 */
    @Override
    @Transactional
    public Result<String> cancelOrder(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) return Result.error("订单不存在");

            Integer status = order.getStatus();
            if (status != 1 && status != 2)
                return Result.error("该状态无法取消订单");

            int result = orderMapper.updateOrderStatusOnly(orderId, 5 );
            return result > 0 ? Result.success("订单取消成功")
                    : Result.error("订单取消失败");

        } catch (Exception e) {
            return Result.error("订单取消失败: " + e.getMessage());
        }
    }


    @Override
    public Result<Order> getOrderByNumber(String orderNumber) {
        try {
            Order order = orderMapper.selectByOrderNumber(orderNumber);
            return order == null ? Result.error("订单不存在")
                    : Result.success(order);
        } catch (Exception e) {
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }


    /** 用户完整订单列表 */
    @Override
    public Result<List<Map<String, Object>>> getUserOrderList(Long userId) {
        try {
            List<Order> orders = orderMapper.selectByUserId(userId);
            if (orders.isEmpty()) return Result.success(Collections.emptyList());

            List<Map<String, Object>> result = new ArrayList<>();

            for (Order order : orders) {
                Restaurant restaurant = restaurantService.getById(order.getRestaurantId());
                List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());

                int totalQuantity = items.stream().mapToInt(OrderItem::getQuantity).sum();

                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("restaurantId", order.getRestaurantId());
                map.put("restaurantName", restaurant != null ? restaurant.getName() : "未知餐厅");
                map.put("restaurantLogo", restaurant != null ? restaurant.getLogoUrl() : null);
                map.put("status", order.getStatus());
                map.put("statusText", getStatusText(order.getStatus()));
                map.put("totalAmount", order.getTotalAmount());
                map.put("totalQuantity", totalQuantity);
                map.put("createdTime", order.getCreatedTime());
                map.put("items", items);

                result.add(map);
            }

            return Result.success(result);

        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 1: return "待支付";
            case 2: return "待处理";
            case 3: return "制作中";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "退款中";
            case 7: return "已退款";
            default: return "未知状态";
        }
    }

    @Override
    public Result<List<Map<String, Object>>> searchOrders(Long userId, String keyword) {

        List<Map<String, Object>> baseList = orderMapper.searchOrders(userId, keyword);

        for (Map<String, Object> map : baseList) {
            Long orderId = ((Number) map.get("orderId")).longValue();

            // 查完整菜品
            List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(orderId);

            map.put("items", items);

            // 对标前端字段
            int totalQuantity = items.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            map.put("totalQuantity", totalQuantity);
            map.put("statusText", getStatusText((Integer) map.get("status")));
        }

        return Result.success(baseList);
    }

    @Override
    public Page<Order> getAdminOrderPage(int pageNum, int pageSize, Long restaurantId, Integer status, String keyword) {

        Page<Order> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();

        if (restaurantId != null) {
            qw.eq(Order::getRestaurantId, restaurantId);
        }

        if (status != null) {
            try {
                status = Integer.valueOf(status + "");
            } catch (Exception ignored) {}

            qw.eq(Order::getStatus, status);
        }

        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Order::getOrderNumber, keyword));
        }

        qw.orderByDesc(Order::getCreatedTime);

        return orderMapper.selectPage(page, qw);
    }

    @Override
    public Result<?> getKitchenOrderList(Long restaurantId) {

        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();

        //  只有不为 null 时才按餐厅过滤
        if (restaurantId != null) {
            qw.eq(Order::getRestaurantId, restaurantId);
        }

        // 只看待处理 + 制作中
        qw.in(Order::getStatus, Arrays.asList(2, 3));
        qw.orderByAsc(Order::getStatus)
                .orderByDesc(Order::getCreatedTime);

        List<Order> orders = orderMapper.selectList(qw);

        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());
            order.setOrderItems(items);
        }

        return Result.success(orders);
    }

    @Override
    public Result<String> requestRefund(Long orderId, String reason, String remark) {

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!(order.getStatus() == 3 || order.getStatus() == 4)) {
            return Result.error("当前状态无法申请退款");
        }

        // 插入退款记录
        refundService.createRefund(
                orderId,
                order.getUserId(),
                order.getRestaurantId(),
                reason,
                remark
        );

        // 更新订单状态 → 6 = 退款中
        orderMapper.updateOrderStatusOnly(orderId, 6);

        return Result.success("退款申请已提交");
    }

    @Override
    public Result<String> approveRefund(Long orderId) {

        refundService.approveRefund(orderId);

        // 退款成功 → 7
        orderMapper.updateOrderStatusOnly(orderId, 7);

        return Result.success("退款已同意");
    }

    @Override
    public Result<String> rejectRefund(Long orderId) {

        // refundService 内部会恢复 previous_status
        refundService.rejectRefund(orderId);

        return Result.success("退款已拒绝");
    }



}
