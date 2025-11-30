// RestaurantService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

public interface RestaurantService extends IService<Restaurant> {

    /**
     * 获取营业中的餐厅列表
     * @return 餐厅列表结果
     */
    Result<List<Restaurant>> getActiveRestaurants();

    /**
     * 获取餐厅详情（包含营业时间、分类和菜品信息）
     * @param id 餐厅ID
     * @return 餐厅详情结果
     */
    Result<Restaurant> getRestaurantDetail(Long id);

    /**
     * 获取所有餐厅列表
     * @return 餐厅列表结果
     */
    Result<List<Restaurant>> getAllRestaurants();

    /**
     * 获取餐厅分类
     * @return 餐厅分类结果
     */
    Result<List<Restaurant>> getByCategory(Integer categoryId);
}