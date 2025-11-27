package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.mapper.CartMapper;
import com.jiumo.weicanjie.mapper.DishMapper;
import com.jiumo.weicanjie.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public Result<List<Cart>> getUserCart(Long userId, Long restaurantId) {
        try {
            if (userId == null || restaurantId == null) {
                return Result.error("参数不能为空");
            }

            List<Cart> cartItems = cartMapper.selectCartWithDishInfo(userId, restaurantId);
            log.info("获取用户购物车，userId: {}, restaurantId: {}, 商品数量: {}",
                    userId, restaurantId, cartItems.size());

            return Result.success(cartItems);
        } catch (Exception e) {
            log.error("获取用户购物车异常", e);
            return Result.error("获取购物车失败");
        }
    }

    @Override
    @Transactional
    public Result<Cart> addToCart(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        try {
            if (userId == null || restaurantId == null || dishId == null || quantity == null || quantity <= 0) {
                return Result.error("参数错误");
            }

            // 检查菜品是否存在且库存充足
            Dish dish = dishMapper.selectById(dishId);
            if (dish == null) {
                return Result.error("菜品不存在");
            }

            if (dish.getStock() < quantity) {
                return Result.error("库存不足");
            }

            // 检查是否已存在购物车记录
            LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Cart::getUserId, userId)
                    .eq(Cart::getRestaurantId, restaurantId)
                    .eq(Cart::getDishId, dishId);

            Cart existingCart = getOne(queryWrapper);

            if (existingCart != null) {
                // 更新数量
                int newQuantity = existingCart.getQuantity() + quantity;
                if (dish.getStock() < newQuantity) {
                    return Result.error("库存不足");
                }
                existingCart.setQuantity(newQuantity);
                existingCart.setUpdatedTime(LocalDateTime.now());
                updateById(existingCart);
                log.info("更新购物车商品数量，userId: {}, dishId: {}, 新数量: {}",
                        userId, dishId, newQuantity);
                return Result.success(existingCart);
            } else {
                // 新增购物车记录
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                newCart.setRestaurantId(restaurantId);
                newCart.setDishId(dishId);
                newCart.setQuantity(quantity);
                newCart.setPrice(dish.getPrice()); // 记录加入时的价格
                newCart.setCreatedTime(LocalDateTime.now());
                newCart.setUpdatedTime(LocalDateTime.now());

                boolean saved = save(newCart);
                if (saved) {
                    log.info("添加商品到购物车，userId: {}, dishId: {}, quantity: {}",
                            userId, dishId, quantity);
                    return Result.success(newCart);
                } else {
                    return Result.error("添加失败");
                }
            }
        } catch (Exception e) {
            log.error("添加购物车异常", e);
            return Result.error("添加失败");
        }
    }

    @Override
    @Transactional
    public Result<Cart> updateCartItem(Long userId, Long restaurantId, Long dishId, Integer quantity) {
        try {
            if (userId == null || restaurantId == null || dishId == null || quantity == null) {
                return Result.error("参数错误");
            }

            if (quantity < 0) {
                return Result.error("数量不能为负数");
            }

            // 检查菜品库存
            Dish dish = dishMapper.selectById(dishId);
            if (dish == null) {
                return Result.error("菜品不存在");
            }

            if (quantity > 0 && dish.getStock() < quantity) {
                return Result.error("库存不足");
            }

            LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Cart::getUserId, userId)
                    .eq(Cart::getRestaurantId, restaurantId)
                    .eq(Cart::getDishId, dishId);

            Cart cartItem = getOne(queryWrapper);

            if (cartItem == null) {
                // 如果购物车商品不存在，返回错误信息，让前端调用添加接口
                return Result.error("购物车商品不存在");
            }

            if (quantity == 0) {
                // 数量为0时删除记录
                removeById(cartItem.getId());
                log.info("删除购物车商品，userId: {}, dishId: {}", userId, dishId);
                return Result.success(null);
            } else {
                // 更新数量
                cartItem.setQuantity(quantity);
                cartItem.setUpdatedTime(LocalDateTime.now());
                updateById(cartItem);
                log.info("更新购物车商品数量，userId: {}, dishId: {}, 新数量: {}",
                        userId, dishId, quantity);
                return Result.success(cartItem);
            }
        } catch (Exception e) {
            log.error("更新购物车异常", e);
            return Result.error("更新失败");
        }
    }

    @Override
    @Transactional
    public Result<String> removeFromCart(Long userId, Long restaurantId, Long dishId) {
        try {
            if (userId == null || restaurantId == null || dishId == null) {
                return Result.error("参数错误");
            }

            LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Cart::getUserId, userId)
                    .eq(Cart::getRestaurantId, restaurantId)
                    .eq(Cart::getDishId, dishId);

            boolean removed = remove(queryWrapper);
            if (removed) {
                log.info("从购物车移除商品，userId: {}, dishId: {}", userId, dishId);
                return Result.success("移除成功");
            } else {
                // 如果商品不存在，也返回成功
                log.info("购物车商品不存在，userId: {}, dishId: {}", userId, dishId);
                return Result.success("商品不存在");
            }
        } catch (Exception e) {
            log.error("移除购物车商品异常", e);
            return Result.error("移除失败");
        }
    }

    @Override
    @Transactional
    public Result<String> clearCart(Long userId, Long restaurantId) {
        try {
            if (userId == null || restaurantId == null) {
                return Result.error("参数错误");
            }

            int deleted = cartMapper.deleteByUserAndRestaurant(userId, restaurantId);
            log.info("清空用户购物车，userId: {}, restaurantId: {}, 删除记录数: {}",
                    userId, restaurantId, deleted);

            return Result.success("清空成功");
        } catch (Exception e) {
            log.error("清空购物车异常", e);
            return Result.error("清空失败");
        }
    }

    @Override
    public Result<Map<Long, Integer>> getCartItemMap(Long userId, Long restaurantId) {
        try {
            if (userId == null || restaurantId == null) {
                return Result.error("参数错误");
            }

            List<Cart> cartItems = cartMapper.selectCartWithDishInfo(userId, restaurantId);
            Map<Long, Integer> cartMap = cartItems.stream()
                    .collect(Collectors.toMap(Cart::getDishId, Cart::getQuantity));

            log.info("获取购物车商品映射，userId: {}, restaurantId: {}, 商品数量: {}",
                    userId, restaurantId, cartMap.size());

            return Result.success(cartMap);
        } catch (Exception e) {
            log.error("获取购物车映射异常", e);
            return Result.error("获取失败");
        }
    }
}