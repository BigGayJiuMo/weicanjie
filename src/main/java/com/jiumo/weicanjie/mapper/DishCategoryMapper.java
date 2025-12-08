package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.DishCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 菜品分类数据访问接口（Mapper），提供对 category 表的操作。
 * 包括通过餐厅ID查询该餐厅的有效菜品分类信息。
 */
@Mapper
public interface DishCategoryMapper extends BaseMapper<DishCategory> {

    /**
     * 根据餐厅ID查询该餐厅所有有效的菜品分类，按排序顺序排列。
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅下所有状态为1的菜品分类列表，按sort_order排序
     */
    @Select("SELECT * FROM category WHERE restaurant_id = #{restaurantId} AND status = 1 ORDER BY sort_order")
    List<DishCategory> selectByRestaurantId(Long restaurantId);
}
