package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.RestaurantImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RestaurantImageMapper extends BaseMapper<RestaurantImage> {

    @Select("SELECT * FROM restaurant_images " +
            "WHERE restaurant_id = #{restaurantId} " +
            "ORDER BY sort_order ASC, id ASC " +
            "LIMIT 3")
    List<RestaurantImage> selectTop3ByRestaurantId(Long restaurantId);
}
