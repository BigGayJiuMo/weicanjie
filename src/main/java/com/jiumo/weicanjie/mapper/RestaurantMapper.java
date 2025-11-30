// RestaurantMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {

    @Select("SELECT * FROM restaurant WHERE status = 1 ORDER BY id")
    List<Restaurant> selectActiveRestaurants();

    //获取所有餐厅的完整方法
    @Select("SELECT id, name, description, contact_phone, logo_url, address, status," +
            "category_type, avg_rating, monthly_sales, min_order_amount, delivery_fee," +
            "packing_fee, delivery_time, business_status, created_time, updated_time " +
            "FROM restaurant ORDER BY id")
    List<Restaurant> selectAllRestaurants();

    //分类页获取所有餐厅的完整方法
    @Select("SELECT * FROM restaurant WHERE category_type = #{categoryId} ORDER BY id")
    List<Restaurant> selectByCategory(Integer categoryId);
}