// DishService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;

public interface DishService {

    /**
     * 根据菜品ID获取菜品详情
     * @param id 菜品ID
     * @return 返回菜品详情
     */
    Result<Dish> getById(Long id);

    /**
     * 分页查询餐厅菜品列表，支持按餐厅、分类和关键词筛选
     * @param restaurantId 餐厅ID
     * @param categoryId 菜品分类ID
     * @param keyword 搜索关键词（可为菜品名称或描述）
     * @param pageNum 当前页码
     * @param pageSize 每页显示的菜品数量
     * @return 返回菜品分页结果
     */
    Page<Dish> getPage(Long restaurantId, Long categoryId, String keyword, int pageNum, int pageSize);

    /**
     * 添加新菜品
     * @param dish 菜品实体
     */
    void addDish(Dish dish);

    /**
     * 更新现有菜品的信息
     * @param dish 菜品实体
     */
    void updateDish(Dish dish);

    /**
     * 切换菜品的状态（启用/禁用）
     * @param id 菜品ID
     */
    void toggleStatus(Long id);

    /**
     * 删除指定菜品
     * @param id 菜品ID
     */
    void deleteDish(Long id);
}
