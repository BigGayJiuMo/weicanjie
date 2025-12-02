package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Override
    @Transactional
    public Result<Order> createOrder(OrderRequest.OrderDTO orderDTO, List<OrderRequest.OrderItemRequest> items) {
        try {
            // 生成订单号
            String orderNumber = "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);

            // 创建订单
            Order order = new Order();
            BeanUtils.copyProperties(orderDTO, order);

            order.setOrderNumber(orderNumber);
            order.setStatus(1); // 待支付
            order.setPayStatus(0); // 未支付
            order.setCreatedTime(LocalDateTime.now());
            order.setUpdatedTime(LocalDateTime.now());
            // 配送费
            if (orderDTO.getDeliveryFee() == null) {
                order.setDeliveryFee(BigDecimal.ZERO);
            } else {
                order.setDeliveryFee(orderDTO.getDeliveryFee());
            }

            // 打包费
            if (orderDTO.getPackingFee() == null) {
                order.setPackingFee(BigDecimal.ZERO);
            } else {
                order.setPackingFee(orderDTO.getPackingFee());
            }

            // 总金额（如果前端传了就写入）
            if (orderDTO.getTotalAmount() == null) {
                // 计算总金额：菜品小计 + 配送费 + 打包费
                BigDecimal itemsTotal = items.stream()
                        .map(i -> i.getDishPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal total = itemsTotal
                        .add(order.getDeliveryFee())
                        .add(order.getPackingFee());

                order.setTotalAmount(total);
            } else {
                order.setTotalAmount(orderDTO.getTotalAmount());
            }
            orderMapper.insert(order);

            // 保存订单项
            List<OrderItem> orderItems = items.stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(item, orderItem);
                orderItem.setOrderId(order.getId());
                orderItem.setSubtotal(item.getDishPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                orderItem.setCreatedTime(LocalDateTime.now());
                return orderItem;
            }).collect(Collectors.toList());

            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }

            // 清空购物车
            cartService.clearUserCart(order.getUserId(), order.getRestaurantId());
            UserStats stats = userStatsService.getOne(
                    new LambdaQueryWrapper<UserStats>()
                            .eq(UserStats::getUserId, order.getUserId())
            );

// 若用户统计不存在 → 初始化
            if (stats == null) {
                stats = new UserStats();
                stats.setUserId(order.getUserId());
                stats.setOrderCount(1);
                stats.setFavoriteCount(0);
                stats.setReviewCount(0);
                stats.setTotalSpent(order.getTotalAmount()); // BigDecimal
                userStatsService.save(stats);
            } else {
                // 更新统计
                stats.setOrderCount(stats.getOrderCount() + 1);
                stats.setTotalSpent(stats.getTotalSpent().add(order.getTotalAmount()));
                userStatsService.updateById(stats);
            }
            return Result.success(order);

        } catch (Exception e) {
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<List<Order>> createBatchOrders(List<OrderController.BatchOrderRequest.SingleOrderRequest> orderRequests) {
        try {
            List<Order> orders = new ArrayList<>();

            for (OrderController.BatchOrderRequest.SingleOrderRequest request : orderRequests) {
                OrderRequest.OrderDTO orderDTO = request.getOrder();
                List<OrderRequest.OrderItemRequest> items = request.getItems();

                // 为每个餐厅创建订单
                Result<Order> result = createOrder(orderDTO, items);
                if (result.getCode() != 200) {
                    throw new RuntimeException("创建订单失败: " + result.getMessage());
                }
                orders.add(result.getData());
            }

            // 清空用户所有购物车
            if (!orders.isEmpty()) {
                Long userId = orders.get(0).getUserId();
                cartService.clearAllUserCart(userId);
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
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getOrderFullDetail(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 获取餐厅信息
            Restaurant restaurant = restaurantService.getById(order.getRestaurantId());

            // ★ 获取订单项列表（含图片）
            List<OrderItem> orderItems = orderItemMapper.selectByOrderIdWithDishInfo(orderId);

            // 计算菜品小计
            BigDecimal subTotal = orderItems.stream()
                    .map(item -> item.getDishPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("restaurant", restaurant);
            result.put("orderItems", orderItems);
            result.put("subTotal", subTotal);
            result.put("deliveryFee", order.getDeliveryFee());
            result.put("packingFee", order.getPackingFee());
            result.put("totalAmount", order.getTotalAmount());

            return Result.success(result);

        } catch (Exception e) {
            return Result.error("获取订单完整详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> updateOrderStatus(Long orderId, Integer status) {
        try {
            int result = orderMapper.updateOrderStatus(orderId, status);
            if (result > 0) {
                return Result.success("订单状态更新成功");
            } else {
                return Result.error("订单状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> simulateWechatPay(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            if (order.getStatus() != 1) {
                return Result.error("订单状态异常，无法支付");
            }

            // 模拟支付成功
            String transactionId = "WX" + System.currentTimeMillis();

            int result = orderMapper.updateOrderPaymentStatus(
                    orderId,
                    2, // 待处理状态
                    1, // 已支付
                    transactionId
            );

            if (result > 0) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> cancelPayment(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            if (order.getStatus() != 1) {
                return Result.error("订单状态异常，无法取消支付");
            }

            // 取消支付，将订单状态改为已取消
            int result = orderMapper.updateOrderStatus(orderId, 5); // 已取消
            if (result > 0) {
                return Result.success("支付已取消");
            } else {
                return Result.error("取消支付失败");
            }
        } catch (Exception e) {
            return Result.error("取消支付失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> cancelOrder(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            Integer status = order.getStatus();
            // 只有：1-待支付，2-待处理 可以取消
            if (status == null || (status != 1 && status != 2)) {
                return Result.error("当前订单状态无法取消");
            }

            // 这里简单处理：直接把订单状态改为 已取消(5)
            int result = orderMapper.updateOrderStatus(orderId, 5);
            if (result > 0) {
                return Result.success("订单取消成功");
            } else {
                return Result.error("订单取消失败");
            }
        } catch (Exception e) {
            return Result.error("订单取消失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Order> getOrderByNumber(String orderNumber) {
        try {
            Order order = orderMapper.selectByOrderNumber(orderNumber);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getUserOrderList(Long userId) {
        try {
            // 1. 获取用户订单（按时间排序）
            List<Order> orders = orderMapper.selectByUserId(userId);
            if (orders.isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            List<Map<String, Object>> result = new ArrayList<>();

            for (Order order : orders) {

                // 2. 餐厅信息
                Restaurant restaurant = restaurantService.getById(order.getRestaurantId());

                // 3. 查询订单项（含图片）
                List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());

                // 3.1 转为前端结构
                List<Map<String, Object>> itemList = items.stream().map(i -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("name", i.getDishName());
                    itemMap.put("price", i.getDishPrice());
                    itemMap.put("quantity", i.getQuantity());
                    itemMap.put("subtotal", i.getSubtotal());

                    // ⭐★ 订单项图片（你 mapper 里已经 SELECT 了 dish.image_url AS dishImageUrl）
                    itemMap.put("imageUrl", i.getDishImageUrl());

                    return itemMap;
                }).collect(Collectors.toList());

                // 4. 计算菜品总数量
                int totalQuantity = items.stream()
                        .mapToInt(OrderItem::getQuantity)
                        .sum();

                // 5. 拼装返回结构
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("restaurantId", order.getRestaurantId());

                // ⭐★ 添加餐厅名称
                map.put("restaurantName", restaurant != null ? restaurant.getName() : "未知餐厅");

                // ⭐★ 添加餐厅 LOGO
                map.put("restaurantLogo", restaurant != null ? restaurant.getLogoUrl() : null);

                map.put("status", order.getStatus());
                map.put("statusText", getStatusText(order.getStatus()));

                map.put("totalAmount", order.getTotalAmount());
                map.put("totalQuantity", totalQuantity);

                // ⭐ createdTime（前端用于格式化）
                map.put("createdTime", order.getCreatedTime());

                // 菜品列表
                map.put("items", itemList);

                result.add(map);
            }

            return Result.success(result);

        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }


    /**
     * 订单状态文本转换
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知状态";
        switch (status) {
            case 1: return "待支付";
            case 2: return "待处理";
            case 3: return "配送中";
            case 4: return "已完成";
            case 5: return "已取消";
            default: return "未知状态";
        }
    }
}