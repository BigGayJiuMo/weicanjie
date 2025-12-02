// RestaurantServiceImpl.java
package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.*;
import com.jiumo.weicanjie.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private DishCategoryMapper dishCategoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private BusinessHoursMapper businessHoursMapper;

    @Autowired
    private RestaurantImageMapper restaurantImageMapper;


    @Override
    public Result<List<Restaurant>> getActiveRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantMapper.selectActiveRestaurants();
            return Result.success(restaurants);
        } catch (Exception e) {
            log.error("获取营业餐厅列表异常", e);
            return Result.error("获取餐厅列表失败");
        }
    }

    @Override
    public Result<Restaurant> getRestaurantDetail(Long id) {
        try {
            // 1. 查询餐厅基本信息
            Restaurant restaurant = restaurantMapper.selectById(id);
            if (restaurant == null) {
                return Result.error("餐厅不存在");
            }

            // 2. 获取营业时间信息
            List<BusinessHours> businessHours = businessHoursMapper.selectByRestaurantId(id);
            restaurant.setBusinessHours(businessHours);

            // 3. 获取分类及菜品信息
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(id);
            for (DishCategory dishCategory : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(dishCategory.getId());
                dishCategory.setDishes(dishes);
            }
            restaurant.setCategories(categories);

            return Result.success(restaurant);

        } catch (Exception e) {
            log.error("获取餐厅详情异常", e);
            return Result.error("获取餐厅详情失败");
        }
    }


    @Override
    public Result<List<Restaurant>> getAllRestaurants() {
        try {
            // 使用新的查询方法获取所有餐厅
            List<Restaurant> restaurants = restaurantMapper.selectAllRestaurants();
            return Result.success(restaurants);
        } catch (Exception e) {
            log.error("获取所有餐厅列表异常", e);
            return Result.error("获取餐厅列表失败");
        }
    }

    @Override
    public Result<List<Restaurant>> getByCategory(Integer categoryId) {
        try {
            List<Restaurant> list = restaurantMapper.selectByCategory(categoryId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("按分类查询餐厅失败", e);
            return Result.error("查询失败");
        }
    }
}