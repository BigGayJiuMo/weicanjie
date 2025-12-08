package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.RestaurantImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 餐厅图片数据访问接口（Mapper），提供对 restaurant_images 表的操作。
 * 主要功能是根据餐厅ID查询该餐厅的所有图片URL，并按排序顺序返回。
 */
@Mapper
public interface RestaurantImageMapper extends BaseMapper<RestaurantImage> {

    /**
     * 根据餐厅ID查询所有图片的URL，结果按排序顺序返回。
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅的所有图片URL，按排序顺序（sort_order、id）升序排列
     */
    @Select(
            "SELECT image_url " +
                    "FROM restaurant_images " +
                    "WHERE restaurant_id = #{restaurantId} " +
                    "ORDER BY sort_order ASC, id ASC"
    )
    List<String> selectImagesByRestaurantId(Long restaurantId);
}
