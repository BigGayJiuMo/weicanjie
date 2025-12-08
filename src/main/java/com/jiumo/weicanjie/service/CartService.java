package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.common.Result;
import java.util.List;
import java.util.Map;

public interface CartService extends IService<Cart> {

    /**
     * 获取指定用户的购物车列表
     * @param userId 用户ID
     * @return 返回用户的购物车列表
     */
    Result<List<Cart>> getUserCartList(Long userId);

    /**
     * 获取指定用户在指定餐厅的购物车映射（每个菜品及其数量）
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回餐厅中的购物车映射，键为菜品ID，值为数量
     */
    Result<Map<Long, Integer>> getUserCartMap(Long userId, Long restaurantId);

    /**
     * 将商品添加到购物车
     * @param cartList 购物车商品列表
     * @return 返回操作结果（成功或失败）
     */
    Result<String> saveCart(List<Cart> cartList);

    /**
     * 更新购物车中某个商品的数量
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @param quantity 新的数量
     * @return 返回操作结果（成功或失败）
     */
    Result<String> updateCartQuantity(Long userId, Long restaurantId, Long dishId, Integer quantity);

    /**
     * 从购物车中移除某个商品
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @return 返回操作结果（成功或失败）
     */
    Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId);

    /**
     * 清空指定用户在指定餐厅的购物车
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果（成功或失败）
     */
    Result<String> clearUserCart(Long userId, Long restaurantId);

    /**
     * 清空指定用户的所有购物车项
     * @param userId 用户ID
     * @return 返回操作结果（成功或失败）
     */
    Result<String> clearAllUserCart(Long userId);

    /**
     * 删除指定餐厅的所有购物车项
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果（成功或失败）
     */
    Result<String> removeRestaurantCart(Long userId, Long restaurantId);

    /**
     * 获取指定用户在指定餐厅的购物车映射（每个菜品及其数量）
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回指定餐厅的购物车项映射，键为菜品ID，值为数量
     */
    Result<Map<Long, Integer>> getUserCart(Long userId, Long restaurantId);

    /**
     * 更新购物车中某个菜品的数量
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @param quantity 新的数量
     * @return 返回操作结果（成功或失败）
     */
    Result<String> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity);

    /**
     * 获取用户购物车中的每个菜品及其数量的映射
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回用户购物车项映射，键为菜品ID，值为数量
     */
    Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId);
}
