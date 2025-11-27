package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Cart;
import com.jiumo.weicanjie.service.CartService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取用户购物车
     */
    @GetMapping("/user/{userId}/restaurant/{restaurantId}")
    public Result<List<Cart>> getUserCart(
            @PathVariable Long userId,
            @PathVariable Long restaurantId) {
        return cartService.getUserCart(userId, restaurantId);
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public Result<Cart> addToCart(@RequestBody AddCartRequest request) {
        return cartService.addToCart(request.getUserId(), request.getRestaurantId(),
                request.getDishId(), request.getQuantity());
    }

    /**
     * 更新购物车商品数量
     */
    @PostMapping("/update")
    public Result<Cart> updateCartItem(@RequestBody UpdateCartRequest request) {
        return cartService.updateCartItem(request.getUserId(), request.getRestaurantId(),
                request.getDishId(), request.getQuantity());
    }

    /**
     * 从购物车移除商品
     */
    @PostMapping("/remove")
    public Result<String> removeFromCart(@RequestBody RemoveCartRequest request) {
        return cartService.removeFromCart(request.getUserId(), request.getRestaurantId(),
                request.getDishId());
    }

    /**
     * 清空购物车
     */
    @PostMapping("/clear")
    public Result<String> clearCart(@RequestBody ClearCartRequest request) {
        return cartService.clearCart(request.getUserId(), request.getRestaurantId());
    }

    /**
     * 获取购物车商品数量映射
     */
    @GetMapping("/map/user/{userId}/restaurant/{restaurantId}")
    public Result<Map<Long, Integer>> getCartItemMap(
            @PathVariable Long userId,
            @PathVariable Long restaurantId) {
        return cartService.getCartItemMap(userId, restaurantId);
    }

    @Data
    public static class AddCartRequest {
        private Long userId;
        private Long restaurantId;
        private Long dishId;
        private Integer quantity;
    }

    @Data
    public static class UpdateCartRequest {
        private Long userId;
        private Long restaurantId;
        private Long dishId;
        private Integer quantity;
    }

    @Data
    public static class RemoveCartRequest {
        private Long userId;
        private Long restaurantId;
        private Long dishId;
    }

    @Data
    public static class ClearCartRequest {
        private Long userId;
        private Long restaurantId;
    }
}