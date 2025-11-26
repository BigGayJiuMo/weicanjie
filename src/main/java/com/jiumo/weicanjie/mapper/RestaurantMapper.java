// RestaurantMapper.java
package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Restaurant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {

    @Select("SELECT * FROM restaurant WHERE status = 1 ORDER BY id")
    List<Restaurant> selectActiveRestaurants();
}