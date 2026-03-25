package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.*;
import com.jiumo.weicanjie.mapper.*;
import com.jiumo.weicanjie.service.RestaurantBusinessHoursService;
import com.jiumo.weicanjie.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            restaurants = restaurants.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() != 0)
                    .collect(Collectors.toList());
            restaurants.forEach(this::loadAndCalculateBusinessStatus);
            return Result.success(restaurants);
        } catch (Exception e) {
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
            Restaurant restaurant = restaurantMapper.selectById(id);
            if (restaurant == null) {
                return Result.error("餐厅不存在");
            }

            loadAndCalculateBusinessStatus(restaurant);

            List<DishCategory> categories = dishCategoryMapper.selectByRestaurantId(id);
            for (DishCategory c : categories) {
                List<Dish> dishes = dishMapper.selectByCategoryId(c.getId());
                c.setDishes(dishes);
            }
            restaurant.setCategories(categories);

            List<String> images = restaurantImageMapper.selectImagesByRestaurantId(id);
            restaurant.setShopImages(images);

            return Result.success(restaurant);

        } catch (Exception e) {
            return Result.error("获取餐厅详情失败");
        }
    }


    private void loadAndCalculateBusinessStatus(Restaurant restaurant) {
        if (restaurant == null) return;

        List<RestaurantBusinessHours> hours =
                restaurantBusinessHoursMapper.selectByRestaurantId(restaurant.getId());
        restaurant.setRestaurantBusinessHours(hours);

        calculateBusinessStatus(restaurant);
    }

    private void applyManualBusinessStatus(Restaurant restaurant) {
        int m = restaurant.getManualBusinessStatus();

        if (m == 1) {
            restaurant.setBusinessStatus(1);
            restaurant.setBusinessStatusText("营业中");
            restaurant.setBusinessStatusClass("status-open");
        } else if (m == 2) {
            restaurant.setBusinessStatus(3);
            restaurant.setBusinessStatusText("休息中");
            restaurant.setBusinessStatusClass("status-break");
        }
    }


    private void calculateBusinessStatus(Restaurant restaurant) {

        /* 1）若 status = 0 → 强制停业（最高优先级） */
        if (restaurant.getStatus() != null && restaurant.getStatus() == 0) {
            restaurant.setBusinessStatus(0);
            restaurant.setBusinessStatusText("已停业");
            restaurant.setBusinessStatusClass("status-closed");
            return;
        }

        /* 2）手动模式（manualBusinessStatus != 0） */
        if (restaurant.getManualBusinessStatus() != null &&
                restaurant.getManualBusinessStatus() != 0) {
            applyManualBusinessStatus(restaurant);
            return;
        }

        /* 3）自动模式：按营业时间判断 */
        int businessStatus = 3;
        String statusText = "休息中";
        String statusClass = "status-break";

        List<RestaurantBusinessHours> hours = restaurant.getRestaurantBusinessHours();
        if (hours != null && !hours.isEmpty()) {

            int today = LocalDate.now().getDayOfWeek().getValue();

            RestaurantBusinessHours todayHours = hours.stream()
                    .filter(h -> h.getDayOfWeek() == today)
                    .findFirst().orElse(null);

            if (todayHours != null && todayHours.getIsOpen() == 1) {
                LocalTime open = todayHours.getOpenTime();
                LocalTime close = todayHours.getCloseTime();
                LocalTime now = LocalTime.now();

                boolean crossDay = close.isBefore(open);

                if (crossDay) {
                    if (now.isAfter(open) || now.isBefore(close)) {
                        businessStatus = 1;
                        statusText = "营业中";
                        statusClass = "status-open";
                    }
                } else {
                    if (now.isBefore(open)) {
                        businessStatus = 2;
                        statusText = "未营业";
                        statusClass = "status-break";
                    } else if (now.isAfter(close)) {
                        businessStatus = 3;
                        statusText = "休息中";
                        statusClass = "status-break";
                    } else {
                        businessStatus = 1;
                        statusText = "营业中";
                        statusClass = "status-open";
                    }
                }
            }
        }

        restaurant.setBusinessStatus(businessStatus);
        restaurant.setBusinessStatusText(statusText);
        restaurant.setBusinessStatusClass(statusClass);
    }

    /**
     * 获取餐厅状态信息
     */
    @Override
    public Result<Integer> getRestaurantStatus(Long restaurantId) {
        try {
            Restaurant restaurant = restaurantMapper.selectById(restaurantId);
            if (restaurant == null) {
                return Result.error("餐厅不存在");
            }

            // 计算并返回业务状态
            loadAndCalculateBusinessStatus(restaurant);
            return Result.success(restaurant.getBusinessStatus());
        } catch (Exception e) {
            log.error("获取餐厅状态失败", e);
            return Result.error("获取餐厅状态失败");
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
            restaurants = restaurants.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() != 0)
                    .collect(Collectors.toList());

            restaurants.forEach(this::loadAndCalculateBusinessStatus);
            return Result.success(restaurants);
        } catch (Exception e) {
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
            list = list.stream()
                    .filter(r -> r.getStatus() != 0)
                    .collect(Collectors.toList());
            list.forEach(this::loadAndCalculateBusinessStatus);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询失败");
        }
    }

    @Override
    public Result<?> getPageByCategory(Integer categoryId, Integer pageNum, Integer pageSize) {
        try {
            Page<Restaurant> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Restaurant> qw = new QueryWrapper<>();

            // 根据分类ID关联查询（假设餐厅表中直接有 category_id 字段）
            // 实际情况请根据数据库结构调整查询条件
            qw.eq("category_type", categoryId);

            // 过滤已下架的餐厅（status != 0）
            qw.ne("status", 0);

            qw.orderByDesc("id"); // 按ID倒序，可根据需要调整

            Page<Restaurant> result = restaurantMapper.selectPage(page, qw);

            // 计算营业状态
            result.getRecords().forEach(this::loadAndCalculateBusinessStatus);

            // 用户端：过滤停业 + 排序
            List<Restaurant> sorted =
                    sortRestaurants(result.getRecords(), true);

            result.setRecords(sorted);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取分类餐厅分页失败", e);
            return Result.error("获取分类餐厅失败");
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
            List<Restaurant> list = restaurantMapper.searchRestaurant(keyword);
            list = list.stream()
                    .filter(r -> r.getStatus() != 0)
                    .collect(Collectors.toList());
            list.forEach(this::loadAndCalculateBusinessStatus);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("搜索失败");
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
            List<Restaurant> list = restaurantMapper.suggestRestaurant(keyword);
            list = list.stream()
                    .filter(r -> r.getStatus() != 0)
                    .collect(Collectors.toList());
            list.forEach(this::loadAndCalculateBusinessStatus);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("联想失败");
        }
    }

    /**
     * 餐厅排序方法
     * @param list 原始列表
     * @param filterClosed 是否过滤停业（true=用户端，false=管理端）
     */
    private List<Restaurant> sortRestaurants(
            List<Restaurant> list,
            boolean filterClosed
    ) {
        return list.stream()
                // 是否过滤停业
                .filter(r -> !filterClosed || r.getBusinessStatus() != 0)
                .sorted((a, b) -> Integer.compare(
                        a.getBusinessStatus(),
                        b.getBusinessStatus()
                ))
                .collect(Collectors.toList());
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

        result.getRecords().forEach(this::loadAndCalculateBusinessStatus);

        // 用户端：过滤停业
        List<Restaurant> sorted =
                sortRestaurants(result.getRecords(), true);

        result.setRecords(sorted);
        return Result.success(result);
    }

    @Override
    public Result<?> getAdminPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<Restaurant> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Restaurant> qw = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            qw.like("name", keyword);
        }

        qw.orderByDesc("id");

        Page<Restaurant> result = restaurantMapper.selectPage(page, qw);

        result.getRecords().forEach(this::loadAndCalculateBusinessStatus);

        // 管理端：不过滤停业
        List<Restaurant> sorted =
                sortRestaurants(result.getRecords(), false);

        result.setRecords(sorted);
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

    @Override
    public void loadBusinessStatus(Restaurant restaurant) {
        loadAndCalculateBusinessStatus(restaurant);
    }
}
