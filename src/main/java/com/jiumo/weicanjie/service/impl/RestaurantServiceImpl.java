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
import java.util.stream.Collectors;

@Slf4j
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private BusinessHoursMapper businessHoursMapper;

    /**
     * 获取营业中的餐厅列表
     * @return 餐厅列表结果
     */
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

    /**
     * 获取餐厅详情（包含营业时间、分类和菜品信息）
     * @param id 餐厅ID
     * @return 餐厅详情结果
     */
    @Override
    public Result<Restaurant> getRestaurantDetail(Long id) {
        try {
            // 查询餐厅基本信息
            Restaurant restaurant = restaurantMapper.selectById(id);
            if (restaurant == null) {
                return Result.error("餐厅不存在");
            }

            // 获取营业时间信息
            List<BusinessHours> businessHours = businessHoursMapper.selectByRestaurantId(id);
            restaurant.setBusinessHours(businessHours);

            // 获取分类及菜品信息
            List<Category> categories = categoryMapper.selectByRestaurantId(id);
            for (Category category : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(category.getId());
                category.setDishes(dishes);
            }
            restaurant.setCategories(categories);

            return Result.success(restaurant);
        } catch (Exception e) {
            log.error("获取餐厅详情异常", e);
            return Result.error("获取餐厅详情失败");
        }
    }

    /**
     * 获取所有餐厅列表
     * @return 餐厅列表结果
     */
    @Override
    public Result<List<Restaurant>> getAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantMapper.selectList(null);
            return Result.success(restaurants);
        } catch (Exception e) {
            log.error("获取所有餐厅列表异常", e);
            return Result.error("获取餐厅列表失败");
        }
    }
}