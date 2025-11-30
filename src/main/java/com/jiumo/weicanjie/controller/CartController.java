package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.service.CartService;
import com.jiumo.weicanjie.service.DishService;
import com.jiumo.weicanjie.service.RestaurantService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final RestaurantService restaurantService;
    private final DishService dishService;

    @Autowired
    public CartController(CartService cartService, RestaurantService restaurantService, DishService dishService) {
        this.cartService = cartService;
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    // 获取用户购物车列表
    @GetMapping("/user/{userId}/list")
    public Result<List<Cart>> getUserCartList(@PathVariable("userId") Long userId) {
        try {
            Result<List<Cart>> result = cartService.getUserCartList(userId);

            if (result.getCode() == 200 && result.getData() != null) {
                List<Cart> cartList = result.getData();

                // 为每个购物车项填充餐厅信息和菜品信息
                for (Cart cart : cartList) {
                    // 填充餐厅信息
                    if (cart.getRestaurantId() != null && cart.getRestaurant() == null) {
                        try {
                            Restaurant restaurant = restaurantService.getById(cart.getRestaurantId());
                            if (restaurant != null) {
                                cart.setRestaurant(restaurant);
                            }
                        } catch (Exception e) {
                            System.err.println("加载餐厅信息失败: " + e.getMessage());
                        }
                    }

                    // 填充菜品信息
                    if (cart.getDishId() != null && cart.getDish() == null) {
                        try {
                            Result<Dish> dishResult = dishService.getById(cart.getDishId());
                            if (dishResult.getCode() == 200 && dishResult.getData() != null) {
                                cart.setDish(dishResult.getData());
                            }
                        } catch (Exception e) {
                            System.err.println("加载菜品信息失败: " + e.getMessage());
                        }
                    }
                }

                return Result.success(cartList);
            } else {
                return result;
            }
        } catch (Exception e) {
            return Result.error("获取购物车列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户购物车映射
     */
    @GetMapping("/map/user/{userId}/restaurant/{restaurantId}")
    public Result<Map<Long, Integer>> getCartItemMap(@PathVariable Long userId, @PathVariable Long restaurantId) {
        return cartService.getCartItemMap(userId, restaurantId);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/save")
    public Result<String> saveCart(@RequestBody List<Cart> cartList) {
        return cartService.saveCart(cartList);
    }

    /**
     * 更新购物车商品数量
     */
    @PostMapping("/update")
    public Result<String> updateCartItem(@RequestBody CartRequest request) {
        return cartService.updateCartItem(request.getUserId(), request.getRestaurantId(), request.getDishId(), request.getQuantity());
    }

    /**
     * 从购物车移除商品
     */
    @PostMapping("/remove")
    public Result<String> removeCartItem(@RequestBody CartRequest request) {
        return cartService.removeFromCart(request.getUserId(), request.getRestaurantId(), request.getDishId());
    }

    /**
     * 清空购物车（指定餐厅）
     */
    @PostMapping("/clear")
    public Result<String> clearCart(@RequestBody CartRequest request) {
        return cartService.clearUserCart(request.getUserId(), request.getRestaurantId());
    }

    /**
     * 删除整个餐厅的购物车
     */
    @PostMapping("/restaurant/remove")
    public Result<String> removeRestaurantCart(@RequestBody CartRequest request) {
        return cartService.removeRestaurantCart(request.getUserId(), request.getRestaurantId());
    }

    @Data
    public static class CartRequest {
        private Long userId;
        private Long restaurantId;
        private Long dishId;
        private Integer quantity;
        private java.math.BigDecimal price;
        private String notes;
    }
}