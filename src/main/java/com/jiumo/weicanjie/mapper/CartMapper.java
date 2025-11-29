package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Cart> selectByUserId(Long userId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    List<Cart> selectByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId} AND dish_id = #{dishId}")
    Cart selectByUserAndDish(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId, @Param("dishId") Long dishId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    int deleteByUserAndRestaurant(@Param("userId") Long userId, @Param("restaurantId") Long restaurantId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}