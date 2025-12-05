package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.*;
import com.jiumo.weicanjie.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant>
        implements RestaurantService {

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

    @Autowired
    private RestaurantBusinessHoursMapper restaurantBusinessHoursMapper;

    @Autowired
    private UserReviewMapper userReviewMapper;

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

            // 2. 营业时间
            List<BusinessHours> businessHours = businessHoursMapper.selectByRestaurantId(id);
            restaurant.setBusinessHours(businessHours);

            // 3. 菜品分类与菜品
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(id);
            for (DishCategory cat : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(cat.getId());
                cat.setDishes(dishes);
            }
            restaurant.setCategories(categories);

            // 4. 商家图片
            List<String> images = restaurantImageMapper.selectImagesByRestaurantId(id);
            restaurant.setShopImages(images);

            return Result.success(restaurant);

        } catch (Exception e) {
            log.error("获取餐厅详情异常", e);
            return Result.error("获取餐厅详情失败");
        }
    }

    @Override
    public Result<List<Restaurant>> getAllRestaurants() {
        try {
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

    @Override
    public Result<List<Restaurant>> searchRestaurant(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            List<Restaurant> list = restaurantMapper.searchRestaurant(keyword);
            return Result.success(list);

        } catch (Exception e) {
            return Result.error("搜索餐厅失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Restaurant>> suggest(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            // 只返回名称，不返回完整详情
            List<Restaurant> list = restaurantMapper.suggestRestaurant(keyword);

            return Result.success(list);

        } catch (Exception e) {
            return Result.error("联想搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Result<?> getPage(Integer pageNum, Integer pageSize, String keyword) {

        Page<Restaurant> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Restaurant> qw = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            qw.like("name", keyword);
        }

        qw.orderByDesc("id");

        Page<Restaurant> result = restaurantMapper.selectPage(page, qw);

        return Result.success(result);
    }


    @Override
    @Transactional
    public Result<?> deleteRestaurant(Long id) {

        // 0. 删除评价
        userReviewMapper.delete(
                new QueryWrapper<UserReview>().eq("restaurant_id", id)
        );

        // 1. 查分类
        List<DishCategory> categories = dishCategoryMapper.selectList(
                new QueryWrapper<DishCategory>().eq("restaurant_id", id)
        );

        // 2. 删菜品
        for (DishCategory category : categories) {
            dishMapper.delete(
                    new QueryWrapper<Dish>().eq("category_id", category.getId())
            );
        }

        // 3. 删分类
        dishCategoryMapper.delete(
                new QueryWrapper<DishCategory>().eq("restaurant_id", id)
        );

        // 4. 删展示图片
        restaurantImageMapper.delete(
                new QueryWrapper<RestaurantImage>().eq("restaurant_id", id)
        );

        // 5. 删营业时间
        restaurantBusinessHoursMapper.delete(
                new QueryWrapper<RestaurantBusinessHours>().eq("restaurant_id", id)
        );

        // 6. 删除餐厅
        this.removeById(id);

        return Result.success("删除成功");
    }



}
