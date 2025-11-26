// BusinessHoursMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.BusinessHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface BusinessHoursMapper extends BaseMapper<BusinessHours> {

    @Select("SELECT * FROM restaurant_business_hours WHERE restaurant_id = #{restaurantId} ORDER BY day_of_week")
    List<BusinessHours> selectByRestaurantId(Long restaurantId);
}