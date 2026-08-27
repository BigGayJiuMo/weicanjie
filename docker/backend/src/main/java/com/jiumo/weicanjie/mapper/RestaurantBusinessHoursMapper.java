package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.RestaurantBusinessHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 餐厅营业时间数据访问接口（Mapper），提供对 restaurant_business_hours 表的操作。
 * 包括查询指定餐厅的营业时间记录，并按星期几排序。
 */
@Mapper
public interface RestaurantBusinessHoursMapper extends BaseMapper<RestaurantBusinessHours> {

    /**
     * 根据餐厅ID查询该餐厅的营业时间，结果按星期几排序。
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅的所有营业时间记录，按星期几升序排列
     */
    @Select("SELECT * FROM restaurant_business_hours WHERE restaurant_id = #{restaurantId} ORDER BY day_of_week")
    List<RestaurantBusinessHours> selectByRestaurantId(Long restaurantId);
}
