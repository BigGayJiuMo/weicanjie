// DishMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    @Select("SELECT * FROM dish WHERE category_id = #{categoryId} AND status = 1 ORDER BY id")
    List<Dish> selectByCategoryId(Long categoryId);

    @Select("SELECT * FROM dish WHERE restaurant_id = #{restaurantId} AND status = 1 ORDER BY category_id, id")
    List<Dish> selectByRestaurantId(Long restaurantId);
}