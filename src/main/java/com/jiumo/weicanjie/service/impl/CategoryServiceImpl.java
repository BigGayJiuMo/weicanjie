// CategoryServiceImpl.java
package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Category;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.mapper.CategoryMapper;
import com.jiumo.weicanjie.mapper.DishMapper;
import com.jiumo.weicanjie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据餐厅ID获取分类列表
     * @param restaurantId 餐厅ID
     * @return 分类列表结果
     */
    @Override
    public Result<List<Category>> getCategoriesByRestaurant(Long restaurantId) {
        try {
            List<Category> categories = categoryMapper.selectByRestaurantId(restaurantId);
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
    public Result<List<Category>> getCategoriesWithDishes(Long restaurantId) {
        try {
            // 获取餐厅分类列表
            List<Category> categories = categoryMapper.selectByRestaurantId(restaurantId);

            // 为每个分类加载菜品信息
            for (Category category : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(category.getId());
                category.setDishes(dishes);
            }

            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类及菜品异常", e);
            return Result.error("获取数据失败");
        }
    }
}