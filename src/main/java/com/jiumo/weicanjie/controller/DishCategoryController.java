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
     * 根据餐厅ID获取该餐厅的所有菜品分类列表
     *
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的所有菜品分类列表
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Result<List<DishCategory>> getCategoriesByRestaurant(@PathVariable Long restaurantId) {
        return dishCategoryService.getCategoriesByRestaurant(restaurantId);
    }

    /**
     * 获取餐厅的所有菜品分类及其对应的菜品列表
     *
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的菜品分类及其包含的菜品信息
     */
    @GetMapping("/with-dishes/{restaurantId}")
    public Result<List<DishCategory>> getCategoriesWithDishes(@PathVariable Long restaurantId) {
        return dishCategoryService.getCategoriesWithDishes(restaurantId);
    }

}
