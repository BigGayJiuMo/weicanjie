package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.common.Result;
import java.util.List;
import java.util.Map;

public interface CartService extends IService<Cart> {

    /**
     * 获取用户购物车列表
     */
    Result<List<Cart>> getUserCartList(Long userId);

    /**
     * 获取用户购物车映射
     */
    Result<Map<Long, Integer>> getUserCartMap(Long userId, Long restaurantId);

    /**
     * 添加商品到购物车
     */
    Result<String> addToCart(Cart cart);

    /**
     * 更新购物车商品数量
     */
    Result<String> updateCartQuantity(Long userId, Long restaurantId, Long dishId, Integer quantity);

    /**
     * 从购物车移除商品
     */
    Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId);

    /**
     * 清空用户购物车（指定餐厅）
     */
    Result<String> clearUserCart(Long userId, Long restaurantId);

    /**
     * 清空用户所有购物车
     */
    Result<String> clearAllUserCart(Long userId);

    /**
     * 删除整个餐厅的购物车
     */
    Result<String> removeRestaurantCart(Long userId, Long restaurantId);

    // 新增方法：根据用户和餐厅获取购物车
    Result<Map<Long, Integer>> getUserCart(Long userId, Long restaurantId);

    // 新增方法：更新购物车项
    Result<String> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity);

    // 新增方法：获取购物车项映射
    Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId);
}