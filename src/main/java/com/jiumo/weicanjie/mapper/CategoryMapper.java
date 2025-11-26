// CategoryMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT * FROM category WHERE restaurant_id = #{restaurantId} AND status = 1 ORDER BY sort_order")
    List<Category> selectByRestaurantId(Long restaurantId);
}