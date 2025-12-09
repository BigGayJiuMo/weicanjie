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

    // 构造函数，注入所需的服务类
    @Autowired
    public CartController(CartService cartService, RestaurantService restaurantService, DishService dishService) {
        this.cartService = cartService;
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    /**
     * 获取用户的购物车列表，并填充每个购物车项的餐厅信息和菜品信息
     * @param userId 用户ID
     * @return 包含购物车项的列表
     */
    @GetMapping("/user/{userId}/list")
    public Result<List<Cart>> getUserCartList(@PathVariable("userId") Long userId) {
        try {
            // 调用服务获取购物车列表
            Result<List<Cart>> result = cartService.getUserCartList(userId);

            // 如果购物车列表获取成功
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
     * 获取指定用户在指定餐厅的购物车商品映射
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 用户购物车中每个菜品及其数量的映射
     */
    @GetMapping("/map/user/{userId}/restaurant/{restaurantId}")
    public Result<Map<Long, Integer>> getCartItemMap(@PathVariable Long userId, @PathVariable Long restaurantId) {
        return cartService.getCartItemMap(userId, restaurantId);
    }

    /**
     * 添加商品到购物车
     * @param cartList 购物车列表
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result<String> saveCart(@RequestBody List<Cart> cartList) {
        return cartService.saveCart(cartList);
    }

    /**
     * 更新购物车中某个商品的数量
     * @param request 请求对象，包含用户ID、餐厅ID、菜品ID和数量
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<String> updateCartItem(@RequestBody CartRequest request) {
        return cartService.updateCartItem(request.getUserId(), request.getRestaurantId(), request.getDishId(), request.getQuantity());
    }

    /**
     * 从购物车中移除指定商品
     * @param request 请求对象，包含用户ID、餐厅ID和菜品ID
     * @return 操作结果
     */
    @PostMapping("/remove")
    public Result<String> removeCartItem(@RequestBody CartRequest request) {
        return cartService.removeFromCart(request.getUserId(), request.getRestaurantId(), request.getDishId());
    }

    /**
     * 清空指定餐厅的购物车
     * @param request 请求对象，包含用户ID和餐厅ID
     * @return 操作结果
     */
    @PostMapping("/clear")
    public Result<String> clearCart(@RequestBody CartRequest request) {
        return cartService.clearUserCart(request.getUserId(), request.getRestaurantId());
    }

    /**
     * 删除整个餐厅的购物车
     * @param request 请求对象，包含用户ID和餐厅ID
     * @return 操作结果
     */
    @PostMapping("/restaurant/remove")
    public Result<String> removeRestaurantCart(@RequestBody CartRequest request) {
        return cartService.removeRestaurantCart(request.getUserId(), request.getRestaurantId());
    }

    /**
     * 请求参数类，用于包含购物车操作所需的参数
     */
    @Data
    public static class CartRequest {
        private Long userId; // 用户ID
        private Long restaurantId; // 餐厅ID
        private Long dishId; // 菜品ID
        private Integer quantity; // 商品数量
        private java.math.BigDecimal price; // 商品价格
        private String notes; // 商品备注
    }
}
