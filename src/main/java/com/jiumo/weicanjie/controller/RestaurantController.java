// RestaurantController.java
package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /**
     * 获取所有营业中的餐厅
     */
    @GetMapping("/active")
    public Result<List<Restaurant>> getActiveRestaurants() {
        return restaurantService.getActiveRestaurants();
    }

    /**
     * 获取所有餐厅
     */
    @GetMapping("/all")
    public Result<List<Restaurant>> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    /**
     * 获取餐厅详情（包含分类和菜品）
     */
    @GetMapping("/{id}")
    public Result<Restaurant> getRestaurantDetail(@PathVariable Long id) {
        return restaurantService.getRestaurantDetail(id);
    }

    /**
     * 获取餐厅基本信息
     */
    @GetMapping("/basic/{id}")
    public Result<Restaurant> getRestaurantBasic(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        if (restaurant != null) {
            return Result.success(restaurant);
        } else {
            return Result.error("餐厅不存在");
        }
    }
}