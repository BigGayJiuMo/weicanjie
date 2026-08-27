// DishCategoryService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.DishCategory;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

public interface DishCategoryService extends IService<DishCategory> {

    /**
     * 根据餐厅ID获取该餐厅的所有菜品分类列表
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的菜品分类列表
     */
    Result<List<DishCategory>> getCategoriesByRestaurant(Long restaurantId);

    /**
     * 根据餐厅ID获取菜品分类以及每个分类下的菜品信息
     * @param restaurantId 餐厅ID
     * @return 返回包含分类及其菜品的列表
     */
    Result<List<DishCategory>> getCategoriesWithDishes(Long restaurantId);

    /**
     * 判断指定菜品分类下是否存在菜品
     * @param categoryId 菜品分类ID
     * @return 如果该分类下有菜品，返回 true，否则返回 false
     */
    boolean hasDish(Long categoryId);
}
