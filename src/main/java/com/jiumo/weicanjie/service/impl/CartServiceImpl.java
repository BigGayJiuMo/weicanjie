package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.mapper.CartMapper;
import com.jiumo.weicanjie.service.CartService;
import com.jiumo.weicanjie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实现购物车相关功能的服务类
 * 提供获取、更新、删除、保存购物车项的功能
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RestaurantService restaurantService;
    /**
     * 获取用户指定餐厅的购物车列表，过滤已停业的餐厅
     *
     * @param userId 用户ID
     * @return 用户的购物车列表
     */
    @Override
    public Result<List<Cart>> getUserCartList(Long userId) {
        try {
            // 获取购物车列表（SQL中已过滤已停业餐厅）
            List<Cart> cartList = cartMapper.selectByUserId(userId);

            if (cartList == null || cartList.isEmpty()) {
                return Result.success(Collections.emptyList());  // 使用 Collections.emptyList()
            }

            // 二次验证：确保没有停业餐厅的商品
            cartList = filterClosedRestaurants(cartList);

            return Result.success(cartList);
        } catch (Exception e) {
            return Result.error("获取购物车列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户在指定餐厅的购物车映射，过滤已停业的餐厅
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 用户购物车映射（菜品ID -> 数量）
     */
    @Override
    public Result<Map<Long, Integer>> getUserCartMap(Long userId, Long restaurantId) {
        try {
            // 检查餐厅状态
            Result<Integer> statusResult = restaurantService.getRestaurantStatus(restaurantId);
            if (statusResult.getCode() == 200 && statusResult.getData() == 0) {
                // 餐厅已停业，返回空映射
                return Result.success(Collections.emptyMap());
            }

            List<Cart> cartList = cartMapper.selectByUserAndRestaurant(userId, restaurantId);
            Map<Long, Integer> cartMap = cartList.stream()
                    .collect(Collectors.toMap(Cart::getDishId, Cart::getQuantity));
            return Result.success(cartMap);
        } catch (Exception e) {
            return Result.error("获取购物车映射失败: " + e.getMessage());
        }
    }

    /**
     * 过滤已停业餐厅的购物车项
     */
    private List<Cart> filterClosedRestaurants(List<Cart> cartList) {
        if (cartList == null || cartList.isEmpty()) {
            return cartList;
        }

        // 按餐厅分组
        Map<Long, List<Cart>> cartsByRestaurant = cartList.stream()
                .collect(Collectors.groupingBy(Cart::getRestaurantId));

        List<Cart> filteredCarts = cartsByRestaurant.entrySet().stream()
                .filter(entry -> {
                    Long restaurantId = entry.getKey();
                    try {
                        // 检查餐厅状态
                        Result<Integer> statusResult = restaurantService.getRestaurantStatus(restaurantId);
                        // 只保留状态不为0（非停业）的餐厅
                        return statusResult.getCode() == 200 && statusResult.getData() != 0;
                    } catch (Exception e) {
                        // 如果查询失败，保守起见返回false
                        return false;
                    }
                })
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        return filteredCarts;
    }

    /**
     * 保存购物车项，若购物车为空则直接返回成功
     *
     * @param cartList 购物车项列表
     * @return 保存结果
     */
    @Override
    @Transactional
    public Result<String> saveCart(List<Cart> cartList) {
        try {
            if (cartList == null || cartList.isEmpty()) {
                return Result.success("空购物车，无需保存"); // 如果购物车为空，直接返回
            }

            Long userId = cartList.get(0).getUserId();
            Long restaurantId = cartList.get(0).getRestaurantId();

            // 清空旧购物车（覆盖式）
            cartMapper.deleteByUserAndRestaurant(userId, restaurantId);

            // 重新插入购物车项
            for (Cart cart : cartList) {
                cartMapper.insert(cart);
            }

            return Result.success("保存成功");
        } catch (Exception e) {
            return Result.error("保存购物车失败: " + e.getMessage());
        }
    }

    /**
     * 更新购物车中某个商品的数量
     * 如果数量为0或负数，则从购物车中移除该商品
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @param quantity 新的数量
     * @return 更新结果
     */
    @Override
    @Transactional
    public Result<String> updateCartQuantity(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        try {
            Cart cart = cartMapper.selectByUserAndDish(userId, restaurantId, dishId); // 根据用户、餐厅和菜品查询购物车项
            if (cart == null) {
                return Result.error("购物车商品不存在");
            }

            if (quantity <= 0) {
                // 如果数量为0或负数，移除商品
                cartMapper.deleteById(cart.getId());
            } else {
                // 更新菜品数量
                cart.setQuantity(quantity);
                cartMapper.updateById(cart);
            }

            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新购物车失败: " + e.getMessage());
        }
    }

    /**
     * 从购物车中移除某个商品
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @return 移除结果
     */
    @Override
    @Transactional
    public Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId) {
        try {
            Cart cart = cartMapper.selectByUserAndDish(userId, restaurantId, dishId); // 查找购物车项
            if (cart != null) {
                cartMapper.deleteById(cart.getId()); // 删除该购物车项
            }
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error("移除购物车失败: " + e.getMessage());
        }
    }

    /**
     * 清空用户指定餐厅的购物车
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 清空结果
     */
    @Override
    @Transactional
    public Result<String> clearUserCart(Long userId, Long restaurantId) {
        try {
            cartMapper.deleteByUserAndRestaurant(userId, restaurantId); // 删除指定餐厅的购物车
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    /**
     * 清空用户所有购物车
     *
     * @param userId 用户ID
     * @return 清空结果
     */
    @Override
    @Transactional
    public Result<String> clearAllUserCart(Long userId) {
        try {
            cartMapper.deleteByUserId(userId); // 删除用户所有购物车项
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户指定餐厅的购物车
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 删除结果
     */
    @Override
    @Transactional
    public Result<String> removeRestaurantCart(Long userId, Long restaurantId) {
        try {
            cartMapper.deleteByUserAndRestaurant(userId, restaurantId); // 删除指定餐厅的购物车
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除购物车失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户在指定餐厅的购物车映射（菜品ID -> 数量）
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 用户购物车映射
     */
    @Override
    public Result<Map<Long, Integer>> getUserCart(Long userId, Long restaurantId) {
        // 直接调用已有的方法
        return getUserCartMap(userId, restaurantId);
    }

    /**
     * 更新购物车项（数量）
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @param quantity 新的数量
     * @return 更新结果
     */
    @Override
    public Result<String> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        // 直接调用已有的方法
        return updateCartQuantity(userId, restaurantId, dishId, quantity);
    }

    /**
     * 获取购物车项映射（菜品ID -> 数量）
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 购物车项映射
     */
    @Override
    public Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId) {
        // 直接调用已有的方法
        return getUserCartMap(userId, restaurantId);
    }
}
