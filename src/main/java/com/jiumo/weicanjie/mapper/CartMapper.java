package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车数据访问接口（Mapper），提供对 cart 表的操作。
 * 包括通过用户、餐厅和菜品查询购物车内容，以及删除购物车记录的功能。
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 根据用户ID查询该用户的所有购物车记录，按创建时间降序排列。
     *
     * @param userId 用户ID
     * @return 返回该用户的所有购物车记录列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Cart> selectByUserId(Long userId);

    /**
     * 根据用户ID和餐厅ID查询该用户在指定餐厅的购物车记录。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回该用户在指定餐厅的所有购物车记录列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    List<Cart> selectByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    /**
     * 根据用户ID、餐厅ID和菜品ID查询该用户在指定餐厅的特定菜品的购物车记录。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param dishId 菜品ID
     * @return 返回该用户在指定餐厅中购买的指定菜品的购物车记录（若存在）
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId} AND dish_id = #{dishId}")
    Cart selectByUserAndDish(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId, @Param("dishId") Long dishId);

    /**
     * 根据用户ID和餐厅ID删除该用户在指定餐厅的所有购物车记录。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回删除的记录数，0表示未删除任何记录
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    int deleteByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    /**
     * 根据用户ID删除该用户所有的购物车记录。
     *
     * @param userId 用户ID
     * @return 返回删除的记录数，0表示未删除任何记录
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
