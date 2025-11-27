package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    @Select("SELECT c.*, d.name as dish_name, d.image_url as dish_image_url, " +
            "r.name as restaurant_name, r.logo_url as restaurant_logo_url " +
            "FROM cart c " +
            "LEFT JOIN dish d ON c.dish_id = d.id " +
            "LEFT JOIN restaurant r ON c.restaurant_id = r.id " +
            "WHERE c.user_id = #{userId} AND c.restaurant_id = #{restaurantId}")
    List<Cart> selectByUserAndRestaurant(Long userId, Long restaurantId);

    @Select("SELECT c.*, d.name as dish_name, d.image_url as dish_image_url, d.price as current_price " +
            "FROM cart c " +
            "LEFT JOIN dish d ON c.dish_id = d.id " +
            "WHERE c.user_id = #{userId} AND c.restaurant_id = #{restaurantId}")
    List<Cart> selectCartWithDishInfo(Long userId, Long restaurantId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId}")
    int deleteByUserAndRestaurant(Long userId, Long restaurantId);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId} AND restaurant_id = #{restaurantId} AND dish_id = #{dishId}")
    int existsByUserRestaurantDish(Long userId, Long restaurantId, Long dishId);
}