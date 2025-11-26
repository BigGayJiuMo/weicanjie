// CategoryController.java
package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Category;
import com.jiumo.weicanjie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据餐厅ID获取分类列表
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Result<List<Category>> getCategoriesByRestaurant(@PathVariable Long restaurantId) {
        return categoryService.getCategoriesByRestaurant(restaurantId);
    }

    /**
     * 获取分类及菜品列表
     */
    @GetMapping("/with-dishes/{restaurantId}")
    public Result<List<Category>> getCategoriesWithDishes(@PathVariable Long restaurantId) {
        return categoryService.getCategoriesWithDishes(restaurantId);
    }
}