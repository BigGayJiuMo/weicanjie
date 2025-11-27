package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.common.Result;

import java.util.List;
import java.util.Map;

public interface CartService extends IService<Cart> {

    /**
     * 获取用户购物车
     */
    Result<List<Cart>> getUserCart(Long userId, Long restaurantId);

    /**
     * 添加商品到购物车
     */
    Result<Cart> addToCart(Long userId, Long restaurantId, Long dishId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    Result<Cart> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity);

    /**
     * 从购物车移除商品
     */
    Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId);

    /**
     * 清空用户购物车
     */
    Result<String> clearCart(Long userId, Long restaurantId);

    /**
     * 获取购物车商品数量映射
     */
    Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId);
}