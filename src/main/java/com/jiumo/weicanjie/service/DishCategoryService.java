// CategoryService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.DishCategory;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

public interface DishCategoryService extends IService<DishCategory> {

    /**
     * 根据餐厅ID获取分类列表
     * @param restaurantId 餐厅ID
     * @return 分类列表结果
     */
    Result<List<DishCategory>> getCategoriesByRestaurant(Long restaurantId);

    /**
     * 根据餐厅ID获取分类及菜品信息
     * @param restaurantId 餐厅ID
     * @return 分类及菜品结果
     */
    Result<List<DishCategory>> getCategoriesWithDishes(Long restaurantId);

    /**
     * 后台菜品分类
     */
    boolean hasDish(Long categoryId);
}