package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.RestaurantImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RestaurantImageMapper extends BaseMapper<RestaurantImage> {

    /**
     * 根据餐厅 ID 获取所有图片（只返回 URL）
     */
    @Select(
            "SELECT image_url " +
                    "FROM restaurant_images " +
                    "WHERE restaurant_id = #{restaurantId} " +
                    "ORDER BY sort_order ASC, id ASC"
    )
    List<String> selectImagesByRestaurantId(Long restaurantId);
}
