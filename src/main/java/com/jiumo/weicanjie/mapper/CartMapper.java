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
     * 修改：添加LEFT JOIN restaurant表，过滤掉已停业(status=0)的餐厅
     *
     * @param userId 用户ID
     * @return 返回该用户的所有购物车记录列表
     */
    @Select("SELECT c.* FROM cart c " +
            "LEFT JOIN restaurant r ON c.restaurant_id = r.id " +
            "WHERE c.user_id = #{userId} " +
            "AND (r.status IS NULL OR r.status != 0) " +  // 过滤已停业的餐厅
            "ORDER BY c.created_time DESC")
    List<Cart> selectByUserId(Long userId);

    /**
     * 根据用户ID和餐厅ID查询该用户在指定餐厅的购物车记录。
     * 修改：添加LEFT JOIN restaurant表，过滤掉已停业(status=0)的餐厅
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回该用户在指定餐厅的所有购物车记录列表
     */
    @Select("SELECT c.* FROM cart c " +
            "LEFT JOIN restaurant r ON c.restaurant_id = r.id " +
            "WHERE c.user_id = #{userId} " +
            "AND c.restaurant_id = #{restaurantId} " +
            "AND (r.status IS NULL OR r.status != 0)")  // 过滤已停业的餐厅
    List<Cart> selectByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    /**
     * 根据用户ID、餐厅ID和菜品ID查询该用户在指定餐厅的特定菜品的购物车记录。
     * 注意：这个方法不用于列表显示，只用于更新数量等操作，所以不过滤餐厅状态
     * 即使餐厅已停业，用户也需要能够减少数量或删除商品
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

    /**
     * 清理已停业餐厅的购物车记录
     * 新增方法：用于定时任务或手动清理
     *
     * @return 返回清理的记录数
     */
    @Delete("DELETE c FROM cart c " +
            "LEFT JOIN restaurant r ON c.restaurant_id = r.id " +
            "WHERE r.status = 0 OR r.status IS NULL")  // 清理已停业或餐厅不存在的记录
    int deleteClosedRestaurantCarts();
}