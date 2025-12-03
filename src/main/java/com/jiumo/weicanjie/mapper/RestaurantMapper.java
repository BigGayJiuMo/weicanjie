package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {

    /**
     * 获取营业中的餐厅
     */
    @Select(
            "SELECT id, name, description, contact_phone, logo_url, address, status, " +
                    "       category_type, avg_rating, monthly_sales, packing_fee, " +
                    "       business_status, created_time, updated_time " +
                    "FROM restaurant " +
                    "WHERE status = 1 " +
                    "ORDER BY id"
    )
    List<Restaurant> selectActiveRestaurants();


    /**
     * 获取所有餐厅（完整字段）
     */
    @Select(
            "SELECT id, name, description, contact_phone, logo_url, address, status, " +
                    "       category_type, avg_rating, monthly_sales, packing_fee, " +
                    "       business_status, created_time, updated_time " +
                    "FROM restaurant " +
                    "ORDER BY id"
    )
    List<Restaurant> selectAllRestaurants();


    /**
     * 按分类查询餐厅
     */
    @Select(
            "SELECT id, name, description, contact_phone, logo_url, address, status, " +
                    "       category_type, avg_rating, monthly_sales, packing_fee, " +
                    "       business_status, created_time, updated_time " +
                    "FROM restaurant " +
                    "WHERE category_type = #{categoryId} " +
                    "ORDER BY id"
    )
    List<Restaurant> selectByCategory(Integer categoryId);
}
