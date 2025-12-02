// RestaurantController.java
package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.service.RestaurantImageService;
import com.jiumo.weicanjie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantImageService restaurantImageService;

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

    /**
     * 获取餐厅分类信息
     */
    @GetMapping("/listByCategory")
    public Result<List<Restaurant>> listByCategory(@RequestParam Integer categoryId) {
        return restaurantService.getByCategory(categoryId);
    }

    @PostMapping("/image/add")
    public Result<String> addRestaurantImage(@RequestBody RestaurantImage img) {
        try {
            boolean ok = restaurantImageService.addImage(img);
            return ok ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}