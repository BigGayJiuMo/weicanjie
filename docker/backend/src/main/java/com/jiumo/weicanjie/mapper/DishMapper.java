package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 菜品数据访问接口（Mapper），提供对 dish 表的操作。
 * 包括通过菜品分类ID查询该分类下所有有效菜品的功能。
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据菜品分类ID查询该分类下所有有效的菜品，按菜品ID排序。
     *
     * @param categoryId 菜品分类ID
     * @return 返回该分类下所有状态为1的菜品列表，按菜品ID排序
     */
    @Select("SELECT * FROM dish WHERE category_id = #{categoryId} AND status = 1 ORDER BY id")
    List<Dish> selectByCategoryId(Long categoryId);
}
