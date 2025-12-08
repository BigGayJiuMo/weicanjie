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

/**
 * 餐厅服务实现类，提供餐厅相关业务逻辑的实现。
 * 包括获取餐厅列表、餐厅详情、餐厅分类、搜索餐厅等操作。
 */
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
    private RestaurantImageMapper restaurantImageMapper;

    @Autowired
    private RestaurantBusinessHoursMapper restaurantBusinessHoursMapper;

    @Autowired
    private UserReviewMapper userReviewMapper;

    /**
     * 获取所有营业中的餐厅列表
     *
     * @return 返回营业中的餐厅列表
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
     * 获取餐厅的详细信息，包括营业时间、菜品分类及菜品、餐厅图片等
     *
     * @param id 餐厅ID
     * @return 返回餐厅详情
     */
    @Override
    public Result<Restaurant> getRestaurantDetail(Long id) {
        try {
            // 1. 查询餐厅基本信息
            Restaurant restaurant = restaurantMapper.selectById(id);
            if (restaurant == null) {
                return Result.error("餐厅不存在");
            }

            // 2. 获取营业时间
            List<RestaurantBusinessHours> restaurantBusinessHours = restaurantBusinessHoursMapper.selectByRestaurantId(id);
            restaurant.setRestaurantBusinessHours(restaurantBusinessHours);

            // 3. 获取菜品分类及菜品
            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(id);
            for (DishCategory category : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(category.getId());
                category.setDishes(dishes);
            }
            restaurant.setCategories(categories);

            // 4. 获取餐厅展示图片
            List<String> images = restaurantImageMapper.selectImagesByRestaurantId(id);
            restaurant.setShopImages(images);

            return Result.success(restaurant);
        } catch (Exception e) {
            log.error("获取餐厅详情异常", e);
            return Result.error("获取餐厅详情失败");
        }
    }

    /**
     * 获取所有餐厅的列表
     *
     * @return 返回所有餐厅的列表
     */
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

    /**
     * 根据分类ID获取餐厅列表
     *
     * @param categoryId 分类ID
     * @return 返回指定分类的餐厅列表
     */
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

    /**
     * 根据关键字搜索餐厅
     *
     * @param keyword 搜索的关键字
     * @return 返回符合条件的餐厅列表
     */
    @Override
    public Result<List<Restaurant>> searchRestaurant(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            List<Restaurant> list = restaurantMapper.searchRestaurant(keyword);
            return Result.success(list);
        } catch (Exception e) {
            log.error("搜索餐厅失败", e);
            return Result.error("搜索餐厅失败: " + e.getMessage());
        }
    }

    /**
     * 获取餐厅实时联想搜索结果
     *
     * @param keyword 输入的关键字
     * @return 返回联想的餐厅列表
     */
    @Override
    public Result<List<Restaurant>> suggest(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            List<Restaurant> list = restaurantMapper.suggestRestaurant(keyword);
            return Result.success(list);
        } catch (Exception e) {
            log.error("联想搜索失败", e);
            return Result.error("联想搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取餐厅的分页列表，可以根据关键字进行筛选
     *
     * @param pageNum 当前页码
     * @param pageSize 每页的餐厅数量
     * @param keyword 搜索关键字（可选）
     * @return 返回分页后的餐厅列表
     */
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

    /**
     * 删除指定的餐厅
     *
     * @param id 餐厅ID
     * @return 删除结果
     */
    @Override
    @Transactional
    public Result<?> deleteRestaurant(Long id) {
        try {
            // 0. 删除餐厅相关的所有评价
            userReviewMapper.delete(
                    new QueryWrapper<UserReview>().eq("restaurant_id", id)
            );

            // 1. 删除餐厅的菜品分类
            List<DishCategory> categories = dishCategoryMapper.selectList(
                    new QueryWrapper<DishCategory>().eq("restaurant_id", id)
            );

            // 2. 删除餐厅的所有菜品
            for (DishCategory category : categories) {
                dishMapper.delete(
                        new QueryWrapper<Dish>().eq("category_id", category.getId())
                );
            }

            // 3. 删除餐厅分类
            dishCategoryMapper.delete(
                    new QueryWrapper<DishCategory>().eq("restaurant_id", id)
            );

            // 4. 删除餐厅的展示图片
            restaurantImageMapper.delete(
                    new QueryWrapper<RestaurantImage>().eq("restaurant_id", id)
            );

            // 5. 删除餐厅的营业时间
            restaurantBusinessHoursMapper.delete(
                    new QueryWrapper<RestaurantBusinessHours>().eq("restaurant_id", id)
            );

            // 6. 删除餐厅
            this.removeById(id);

            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除餐厅失败", e);
            return Result.error("删除餐厅失败");
        }
    }
}
