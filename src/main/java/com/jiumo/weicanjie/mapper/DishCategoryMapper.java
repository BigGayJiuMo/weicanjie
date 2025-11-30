// CategoryMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.DishCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DishCategoryMapper extends BaseMapper<DishCategory> {

    @Select("SELECT * FROM category WHERE restaurant_id = #{restaurantId} AND status = 1 ORDER BY sort_order")
    List<DishCategory> selectByRestaurantId(Long restaurantId);
}