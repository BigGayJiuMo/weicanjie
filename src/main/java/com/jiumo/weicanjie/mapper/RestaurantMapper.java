package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 餐厅数据访问接口（Mapper），提供对 restaurant 表的操作。
 * 包括获取所有餐厅、按分类查询餐厅、搜索餐厅等功能。
 */
@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {

    /**
     * 获取所有营业中的餐厅。
     * 只返回状态为1（营业中的餐厅），按餐厅ID升序排列。
     *
     * @return 返回所有营业中的餐厅列表
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
     * 获取所有餐厅的完整信息。
     * 返回所有餐厅的信息，按餐厅ID升序排列。
     *
     * @return 返回所有餐厅的完整列表
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
     * 根据餐厅分类ID查询餐厅。
     * 根据餐厅的分类类型（category_type）过滤餐厅，并按餐厅ID升序排列。
     *
     * @param categoryId 分类ID
     * @return 返回该分类下的餐厅列表
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


    /**
     * 搜索餐厅，支持按餐厅名称或描述进行搜索。
     * 根据关键词（keyword）搜索餐厅的名称或描述，返回餐厅列表。
     *
     * @param keyword 搜索关键词
     * @return 返回匹配的餐厅列表
     */
    List<Restaurant> searchRestaurant(@Param("keyword") String keyword);

    /**
     * 实时联想搜索餐厅，返回餐厅的ID和名称。
     * 根据关键词（keyword）返回餐厅名称匹配的结果，最多返回8条数据。
     *
     * @param keyword 搜索关键词
     * @return 返回匹配的餐厅ID和名称列表（最多8个结果）
     */
    List<Restaurant> suggestRestaurant(@Param("keyword") String keyword);
}
