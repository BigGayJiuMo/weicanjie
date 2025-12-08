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
     *
     * @param restaurantId 餐厅ID
     * @return 包含餐厅分类的结果。如果查询失败，返回错误信息
     */
    @Override
    public Result<List<DishCategory>> getCategoriesByRestaurant(Long restaurantId) {
        try {
            // 获取餐厅的所有分类
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(restaurantId);
            return Result.success(categories);  // 返回成功的分类列表
        } catch (Exception e) {
            log.error("获取餐厅分类异常", e);  // 捕获异常并记录日志
            return Result.error("获取分类失败");  // 返回失败的结果
        }
    }

    /**
     * 根据餐厅ID获取分类及菜品信息
     *
     * @param restaurantId 餐厅ID
     * @return 包含餐厅分类和对应菜品的结果。如果查询失败，返回错误信息
     */
    @Override
    public Result<List<DishCategory>> getCategoriesWithDishes(Long restaurantId) {
        try {
            // 获取餐厅的所有分类
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(restaurantId);

            // 为每个分类加载菜品信息
            for (DishCategory dishCategory : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(dishCategory.getId());
                dishCategory.setDishes(dishes);  // 设置菜品信息到分类中
            }

            return Result.success(categories);  // 返回包含分类和菜品的结果
        } catch (Exception e) {
            log.error("获取分类及菜品异常", e);  // 捕获异常并记录日志
            return Result.error("获取数据失败");  // 返回失败的结果
        }
    }

    /**
     * 检查指定分类是否包含菜品
     *
     * @param categoryId 菜品分类ID
     * @return 如果该分类下有菜品，返回true，否则返回false
     */
    @Override
    public boolean hasDish(Long categoryId) {
        // 根据分类ID查询菜品列表，若有菜品则返回true
        return dishMapper.selectByCategoryId(categoryId).size() > 0;
    }
}
