package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.mapper.CartMapper;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public Result<List<Cart>> getUserCartList(Long userId) {
        try {
            List<Cart> cartList = cartMapper.selectByUserId(userId);
            return Result.success(cartList);
        } catch (Exception e) {
            return Result.error("获取购物车列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Integer>> getUserCartMap(Long userId, Long restaurantId) {
        try {
            List<Cart> cartList = cartMapper.selectByUserAndRestaurant(userId, restaurantId);
            Map<Long, Integer> cartMap = cartList.stream()
                    .collect(Collectors.toMap(Cart::getDishId, Cart::getQuantity));
            return Result.success(cartMap);
        } catch (Exception e) {
            return Result.error("获取购物车映射失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> addToCart(Cart cart) {
        try {
            // 检查是否已存在
            Cart existingCart = cartMapper.selectByUserAndDish(cart.getUserId(), cart.getRestaurantId(), cart.getDishId());
            if (existingCart != null) {
                // 如果已存在，更新数量
                existingCart.setQuantity(existingCart.getQuantity() + cart.getQuantity());
                cartMapper.updateById(existingCart);
                return Result.success("更新成功");
            } else {
                // 新增
                cartMapper.insert(cart);
                return Result.success("添加成功");
            }
        } catch (Exception e) {
            return Result.error("添加购物车失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> updateCartQuantity(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        try {
            Cart cart = cartMapper.selectByUserAndDish(userId, restaurantId, dishId);
            if (cart == null) {
                return Result.error("购物车商品不存在");
            }

            if (quantity <= 0) {
                // 如果数量为0或负数，移除商品
                cartMapper.deleteById(cart.getId());
            } else {
                // 更新数量
                cart.setQuantity(quantity);
                cartMapper.updateById(cart);
            }
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新购物车失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId) {
        try {
            Cart cart = cartMapper.selectByUserAndDish(userId, restaurantId, dishId);
            if (cart != null) {
                cartMapper.deleteById(cart.getId());
            }
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error("移除购物车失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> clearUserCart(Long userId, Long restaurantId) {
        try {
            cartMapper.deleteByUserAndRestaurant(userId, restaurantId);
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> clearAllUserCart(Long userId) {
        try {
            cartMapper.deleteByUserId(userId);
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空购物车失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> removeRestaurantCart(Long userId, Long restaurantId) {
        try {
            cartMapper.deleteByUserAndRestaurant(userId, restaurantId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除购物车失败: " + e.getMessage());
        }
    }

    // 新增方法实现
    @Override
    public Result<Map<Long, Integer>> getUserCart(Long userId, Long restaurantId) {
        // 直接调用已有的方法
        return getUserCartMap(userId, restaurantId);
    }

    @Override
    public Result<String> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        // 直接调用已有的方法
        return updateCartQuantity(userId, restaurantId, dishId, quantity);
    }

    @Override
    public Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId) {
        // 直接调用已有的方法
        return getUserCartMap(userId, restaurantId);
    }
}