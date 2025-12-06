// CategoryServiceImpl.java
package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.DishCategory;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.mapper.DishCategoryMapper;
import com.jiumo.weicanjie.mapper.DishMapper;
import com.jiumo.weicanjie.service.DishCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements DishCategoryService {

    @Autowired
    private DishCategoryMapper dishCategoryMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据餐厅ID获取分类列表
     * @param restaurantId 餐厅ID
     * @return 分类列表结果
     */
    @Override
    public Result<List<DishCategory>> getCategoriesByRestaurant(Long restaurantId) {
        try {
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(restaurantId);
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取餐厅分类异常", e);
            return Result.error("获取分类失败");
        }
    }

    /**
     * 根据餐厅ID获取分类及菜品信息
     * @param restaurantId 餐厅ID
     * @return 分类及菜品结果
     */
    @Override
    public Result<List<DishCategory>> getCategoriesWithDishes(Long restaurantId) {
        try {
            // 获取餐厅分类列表
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(restaurantId);

            // 为每个分类加载菜品信息
            for (DishCategory dishCategory : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(dishCategory.getId());
                dishCategory.setDishes(dishes);
            }

            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类及菜品异常", e);
            return Result.error("获取数据失败");
        }
    }

    /**
     * 后台菜品分类
     */
    @Override
    public boolean hasDish(Long categoryId) {
        return dishMapper.selectByCategoryId(categoryId).size() > 0;
    }


}