package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.*;
import com.jiumo.weicanjie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result<Order> createOrder(Order order, List<Map<String, Object>> items) {
        try {
            // 生成订单号
            String orderNumber = generateOrderNumber();
            order.setOrderNumber(orderNumber);
            order.setStatus(1); // 待支付
            order.setPayStatus(0); // 未支付

            // 保存订单
            boolean saved = save(order);
            if (!saved) {
                return Result.error("创建订单失败");
            }

            // 保存订单项
            List<OrderItem> orderItems = new ArrayList<>();
            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setDishId(Long.valueOf(item.get("dishId").toString()));
                orderItem.setDishName(item.get("dishName").toString());
                orderItem.setDishPrice(new BigDecimal(item.get("dishPrice").toString()));
                orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
                orderItem.setSubtotal(new BigDecimal(item.get("subtotal").toString()));
                orderItem.setCreatedTime(LocalDateTime.now());

                orderItems.add(orderItem);
            }

            // 批量保存订单项
            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }

            order.setOrderItems(orderItems);

            log.info("订单创建成功，订单号: {}, 总金额: {}", orderNumber, order.getTotalAmount());
            return Result.success(order);

        } catch (Exception e) {
            log.error("创建订单异常", e);
            return Result.error("创建订单失败");
        }
    }

    @Override
    public Result<List<Order>> getUserOrders(Long userId) {
        try {
            List<Order> orders = orderMapper.selectByUserId(userId);

            // 为每个订单加载订单项和餐厅信息
            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());
                order.setOrderItems(orderItems);

                Restaurant restaurant = restaurantMapper.selectById(order.getRestaurantId());
                order.setRestaurant(restaurant);
            }

            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取用户订单列表异常", e);
            return Result.error("获取订单列表失败");
        }
    }

    @Override
    public Result<Order> getOrderDetail(Long orderId) {
        try {
            Order order = getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 加载订单项
            List<OrderItem> orderItems = orderItemMapper.selectByOrderIdWithDishInfo(orderId);
            order.setOrderItems(orderItems);

            // 加载餐厅信息
            Restaurant restaurant = restaurantMapper.selectById(order.getRestaurantId());
            order.setRestaurant(restaurant);

            // 加载用户信息
            User user = userMapper.selectById(order.getUserId());
            order.setUser(user);

            return Result.success(order);
        } catch (Exception e) {
            log.error("获取订单详情异常", e);
            return Result.error("获取订单详情失败");
        }
    }

    @Override
    @Transactional
    public Result<String> updateOrderStatus(Long orderId, Integer status) {
        try {
            int updated = orderMapper.updateOrderStatus(orderId, status);
            if (updated > 0) {
                log.info("更新订单状态成功，orderId: {}, status: {}", orderId, status);
                return Result.success("更新成功");
            } else {
                return Result.error("订单不存在");
            }
        } catch (Exception e) {
            log.error("更新订单状态异常", e);
            return Result.error("更新失败");
        }
    }

    @Override
    @Transactional
    public Result<String> simulateWechatPay(Long orderId) {
        try {
            Order order = getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            if (order.getStatus() != 1) {
                return Result.error("订单状态不正确");
            }

            // 模拟微信支付交易号
            String transactionId = "WX" + System.currentTimeMillis() + new Random().nextInt(1000);

            // 更新订单状态为已支付，状态改为待处理
            int updated = orderMapper.updateOrderPaymentStatus(orderId, 2, 1, transactionId);
            if (updated > 0) {
                log.info("模拟微信支付成功，orderId: {}, transactionId: {}", orderId, transactionId);
                return Result.success("支付成功");
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            log.error("模拟微信支付异常", e);
            return Result.error("支付异常");
        }
    }

    @Override
    @Transactional
    public Result<String> cancelPayment(Long orderId) {
        try {
            Order order = getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            if (order.getStatus() != 1) {
                return Result.error("订单状态不正确");
            }

            // 取消支付，订单状态改为已取消
            int updated = orderMapper.updateOrderStatus(orderId, 5);
            if (updated > 0) {
                log.info("取消支付成功，orderId: {}", orderId);
                return Result.success("取消支付成功");
            } else {
                return Result.error("取消支付失败");
            }
        } catch (Exception e) {
            log.error("取消支付异常", e);
            return Result.error("取消支付异常");
        }
    }

    @Override
    public Result<Order> getOrderByNumber(String orderNumber) {
        try {
            Order order = orderMapper.selectByOrderNumber(orderNumber);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 加载订单项
            List<OrderItem> orderItems = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());
            order.setOrderItems(orderItems);

            return Result.success(order);
        } catch (Exception e) {
            log.error("根据订单号查询订单异常", e);
            return Result.error("查询订单失败");
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
}