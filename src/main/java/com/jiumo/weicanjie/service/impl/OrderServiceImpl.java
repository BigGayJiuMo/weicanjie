package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.dto.BatchOrderRequest;
import com.jiumo.weicanjie.dto.OrderRequest;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.OrderItemMapper;
import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.common.Result;
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

    /**
     * 创建单餐厅订单
     * @param orderDTO 订单数据传输对象
     * @param items 订单项数据列表
     * @return 创建结果，包含订单详细信息
     */
    @Override
    @Transactional
    public Result<Order> createOrder(OrderRequest.OrderDTO orderDTO, List<OrderRequest.OrderItemRequest> items) {
        try {
            // 生成唯一的订单号
            String orderNumber = "ORD" + System.currentTimeMillis() + new Random().nextInt(1000);

            // 创建订单对象并填充字段
            Order order = new Order();
            BeanUtils.copyProperties(orderDTO, order);
            order.setOrderNumber(orderNumber);
            order.setStatus(1);         // 状态设置为待支付
            order.setPayStatus(0);      // 初始支付状态为未支付
            order.setCreatedTime(LocalDateTime.now());
            order.setUpdatedTime(LocalDateTime.now());

            // 设置打包费，若未传递则默认为0
            order.setPackingFee(orderDTO.getPackingFee() != null ? orderDTO.getPackingFee() : BigDecimal.ZERO);

            // 计算总金额：若前端未传递totalAmount，则后端计算
            if (orderDTO.getTotalAmount() == null) {
                BigDecimal itemsTotal = items.stream()
                        .map(i -> i.getDishPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal total = itemsTotal.add(order.getPackingFee());
                order.setTotalAmount(total);
            } else {
                order.setTotalAmount(orderDTO.getTotalAmount());
            }

            // 插入订单到数据库
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

            // 批量插入订单项
            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }

            // 从购物车中删除已下单的菜品
            for (OrderRequest.OrderItemRequest item : items) {
                cartService.removeFromCart(order.getUserId(), order.getRestaurantId(), item.getDishId());
            }

            // 更新用户统计信息：订单数和总消费金额
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

            // 返回订单创建成功的结果
            return Result.success(order);

        } catch (Exception e) {
            // 异常处理，返回错误信息
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建多餐厅订单
     * @param list 批量订单请求列表
     * @return 创建结果，包含所有订单详细信息
     */
    @Override
    @Transactional
    public Result<List<Order>> createBatchOrders(List<BatchOrderRequest.SingleOrderRequest> list) {
        try {
            List<Order> orders = new ArrayList<>();

            // 为每个订单请求创建订单
            for (BatchOrderRequest.SingleOrderRequest req : list) {
                OrderRequest.OrderDTO orderDTO = req.getOrder();
                List<OrderRequest.OrderItemRequest> items = req.getItems();

                Result<Order> result = createOrder(orderDTO, items);

                // 若创建失败，抛出异常
                if (result.getCode() != 200) {
                    throw new RuntimeException("创建订单失败: " + result.getMessage());
                }

                orders.add(result.getData());

                // 精确删除购物车中的菜品
                for (OrderRequest.OrderItemRequest item : items) {
                    cartService.removeFromCart(orderDTO.getUserId(), orderDTO.getRestaurantId(), item.getDishId());
                }
            }

            // 返回批量创建订单的成功结果
            return Result.success(orders);

        } catch (Exception e) {
            // 异常处理，返回错误信息
            return Result.error("批量创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @return 用户订单列表
     */
    @Override
    public Result<List<Order>> getUserOrders(Long userId) {
        try {
            List<Order> orders = orderMapper.selectByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            return Result.error("获取用户订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
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

    /**
     * 获取订单完整详情（包含菜品图片等信息）
     * @param orderId 订单ID
     * @return 订单详细信息
     */
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

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新的订单状态
     * @return 更新结果
     */
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

    /**
     * 模拟微信支付
     * @param orderId 订单ID
     * @return 支付结果
     */
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

    /**
     * 取消支付
     * @param orderId 订单ID
     * @return 取消支付结果
     */
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

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消订单结果
     */
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

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     * @return 订单查询结果
     */
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

    /**
     * 获取用户完整的订单列表（含餐厅信息、订单项等）
     * @param userId 用户ID
     * @return 用户完整订单列表
     */
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
                map.put("orderNumber", order.getOrderNumber());
                map.put("createdTime", order.getCreatedTime());
                map.put("items", items);

                result.add(map);
            }

            return Result.success(result);

        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据订单状态获取订单状态的文本表示
     * @param status 订单状态
     * @return 状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 1: return "待支付";
            case 2: return "待处理";
            case 3: return "制作中";
            case 4: return "待取餐";
            case 5: return "已取消";
            case 6: return "已完成";
            case 7: return "退款中";
            case 8: return "已退款";
            default: return "未知状态";
        }
    }

    /**
     * 搜索订单（按餐厅、菜品等关键字）
     * @param userId 用户ID
     * @param keyword 搜索关键字
     * @return 搜索结果
     */
    @Override
    public Result<List<Map<String, Object>>> searchOrders(Long userId, String keyword) {

        List<Map<String, Object>> baseList = orderMapper.searchOrders(userId, keyword);

        for (Map<String, Object> map : baseList) {
            Long orderId = ((Number) map.get("orderId")).longValue();

            // 查找完整的菜品信息
            List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(orderId);

            map.put("items", items);

            // 计算总菜品数量
            int totalQuantity = items.stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            map.put("totalQuantity", totalQuantity);
            map.put("statusText", getStatusText((Integer) map.get("status")));
        }

        return Result.success(baseList);
    }

    /**
     * 获取后台分页订单列表
     * @param pageNum 当前页
     * @param pageSize 每页大小
     * @param restaurantId 餐厅ID
     * @param status 订单状态
     * @param keyword 搜索关键字
     * @return 订单分页列表
     */
    @Override
    public Page<Order> getAdminOrderPage(int pageNum, int pageSize, Long restaurantId, Integer status, String keyword) {

        Page<Order> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();

        // 过滤餐厅ID
        if (restaurantId != null) {
            qw.eq(Order::getRestaurantId, restaurantId);
        }

        // 过滤订单状态
        if (status != null) {
            try {
                status = Integer.valueOf(status + "");
            } catch (Exception ignored) {}

            qw.eq(Order::getStatus, status);
        }

        // 关键字模糊搜索
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like(Order::getOrderNumber, keyword));
        }

        qw.orderByDesc(Order::getCreatedTime);

        return orderMapper.selectPage(page, qw);
    }

    /**
     * 获取厨房订单列表
     * @param restaurantId 餐厅ID
     * @return 厨房订单列表
     */
    @Override
    public Result<?> getKitchenOrderList(Long restaurantId) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<>();

        if (restaurantId != null) {
            qw.eq(Order::getRestaurantId, restaurantId);
        }

        // 后厨需要看到待处理、制作中、待取餐的订单
        qw.in(Order::getStatus, Arrays.asList(2, 3, 4));
        qw.orderByAsc(Order::getStatus)
                .orderByDesc(Order::getCreatedTime);

        List<Order> orders = orderMapper.selectList(qw);

        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectByOrderIdWithDishInfo(order.getId());
            order.setOrderItems(items);
        }

        return Result.success(orders);
    }

    /**
     * 用户申请退款（订单状态变为退款中）
     * @param orderId 订单ID
     * @param reason 退款原因
     * @param remark 退款备注
     * @return 退款申请结果
     */
    @Override
    public Result<String> requestRefund(Long orderId, String reason, String remark) {

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        if (!(order.getStatus() == 4  || order.getStatus() == 6)) {
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

        // 更新订单状态为退款中
        orderMapper.updateOrderStatusOnly(orderId, 7);

        return Result.success("退款申请已提交");
    }

    /**
     * 后台审核并同意退款申请（将订单状态更新为已退款）
     * @param orderId 订单ID
     * @return 同意退款的操作结果
     */
    @Override
    public Result<String> approveRefund(Long orderId) {

        // 调用退款服务，更新退款记录为已同意
        refundService.approveRefund(orderId);

        // 退款成功后，将订单状态更新为已退款（状态码 8）
        orderMapper.updateOrderStatusOnly(orderId, 8);

        // 返回操作成功的信息
        return Result.success("退款已同意");
    }

    /**
     * 后台拒绝退款申请（将订单恢复为原始状态）
     * @param orderId 订单ID
     * @return 拒绝退款的操作结果
     */
    @Override
    public Result<String> rejectRefund(Long orderId) {

        // 调用退款服务，恢复订单的原始状态
        // refundService 内部会恢复订单的 previous_status
        refundService.rejectRefund(orderId);

        // 返回操作成功的信息
        return Result.success("退款已拒绝");
    }

    /**
     * 用户确认取餐
     * @param orderId 订单ID
     * @return 确认取餐结果
     */
    @Override
    @Transactional
    public Result<String> confirmPickup(Long orderId) {
        try {
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 只有待取餐状态（4）才能确认取餐
            if (order.getStatus() != 4) {
                return Result.error("当前状态无法确认取餐");
            }

            // 更新订单状态为已完成（6）
            int result = orderMapper.updateOrderStatusOnly(orderId, 6);

            if (result > 0) {
                // 可选：记录操作日志或其他业务逻辑
                return Result.success("确认取餐成功");
            } else {
                return Result.error("确认取餐失败");
            }

        } catch (Exception e) {
            return Result.error("确认取餐失败: " + e.getMessage());
        }
    }
}
