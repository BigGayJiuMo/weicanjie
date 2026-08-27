package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserFavorite;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏服务接口，提供用户餐厅收藏相关操作。
 * 包含添加、删除、查询收藏信息等功能。
 */
public interface UserFavoriteService extends IService<UserFavorite> {

    /**
     * 添加餐厅到用户的收藏列表。
     * 如果该餐厅已被该用户收藏，则不会重复添加。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果，包括成功或失败的提示信息
     */
    Result<String> addRestaurantFavorite(Long userId, Long restaurantId);

    /**
     * 从用户的收藏列表中移除餐厅。
     * 如果该餐厅没有被用户收藏，则不做任何操作。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果，包括成功或失败的提示信息
     */
    Result<String> removeRestaurantFavorite(Long userId, Long restaurantId);

    /**
     * 判断指定的餐厅是否已被该用户收藏。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回一个布尔值，表示该餐厅是否为该用户的收藏
     */
    boolean isRestaurantFavorite(Long userId, Long restaurantId);

    /**
     * 获取指定用户收藏的餐厅数量。
     *
     * @param userId 用户ID
     * @return 返回该用户收藏的餐厅数量
     */
    Integer getRestaurantFavoriteCount(Long userId);

    /**
     * 获取指定用户收藏的餐厅列表。
     *
     * @param userId 用户ID
     * @return 返回该用户收藏的餐厅列表，包含餐厅的基本信息
     */
    List<Map<String, Object>> getFavoriteRestaurantList(Long userId);
}
