// CategoryController.java
package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.DishCategory;
import com.jiumo.weicanjie.service.DishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class DishCategoryController {

    @Autowired
    private DishCategoryService dishCategoryService;

    /**
     * 根据餐厅ID获取分类列表
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Result<List<DishCategory>> getCategoriesByRestaurant(@PathVariable Long restaurantId) {
        return dishCategoryService.getCategoriesByRestaurant(restaurantId);
    }

    /**
     * 获取分类及菜品列表
     */
    @GetMapping("/with-dishes/{restaurantId}")
    public Result<List<DishCategory>> getCategoriesWithDishes(@PathVariable Long restaurantId) {
        return dishCategoryService.getCategoriesWithDishes(restaurantId);
    }

}