package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.mapper.DishMapper;
import com.jiumo.weicanjie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据菜品ID查询菜品
     *
     * @param id 菜品ID
     * @return 包含菜品信息的结果，如果不存在则返回错误信息
     */
    @Override
    public Result<Dish> getById(Long id) {
        try {
            Dish dish = dishMapper.selectById(id);
            if (dish != null) {
                return Result.success(dish);  // 如果菜品存在，返回成功结果
            } else {
                return Result.error("菜品不存在");  // 如果菜品不存在，返回错误信息
            }
        } catch (Exception e) {
            return Result.error("查询菜品失败: " + e.getMessage());  // 捕获异常并返回错误信息
        }
    }

    /**
     * 获取分页菜品列表
     *
     * @param restaurantId 餐厅ID
     * @param categoryId   分类ID
     * @param keyword      搜索关键字
     * @param pageNum      当前页码
     * @param pageSize     每页显示的数量
     * @return 分页的菜品列表
     */
    @Override
    public Page<Dish> getPage(Long restaurantId, Long categoryId, String keyword, int pageNum, int pageSize) {
        // 创建分页对象
        Page<Dish> page = new Page<>(pageNum, pageSize);

        // 创建查询条件构造器
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getRestaurantId, restaurantId);  // 按照餐厅ID进行过滤
        qw.ne(Dish::getStatus, -1);
        // 如果提供了分类ID，进行分类过滤
        if (categoryId != null) {
            qw.eq(Dish::getCategoryId, categoryId);
        }

        // 如果提供了搜索关键字，进行菜品名称模糊匹配
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Dish::getName, keyword);
        }

        // 按照分类ID和菜品ID排序
        qw.orderByAsc(Dish::getCategoryId).orderByAsc(Dish::getId);

        // 执行查询并返回分页结果
        return dishMapper.selectPage(page, qw);
    }

    /**
     * 添加新菜品
     *
     * @param dish 要添加的菜品对象
     */
    @Override
    public void addDish(Dish dish) {
        dish.setStatus(1);  // 设置默认状态为启用
        dishMapper.insert(dish);  // 插入新菜品到数据库
    }

    /**
     * 更新菜品信息
     *
     * @param dish 要更新的菜品对象
     */
    @Override
    public void updateDish(Dish dish) {
        dishMapper.updateById(dish);  // 根据菜品ID更新菜品信息
    }

    /**
     * 切换菜品状态（启用/禁用）
     *
     * @param id 菜品ID
     */
    @Override
    public void toggleStatus(Long id) {
        Dish dish = dishMapper.selectById(id);  // 查询指定ID的菜品
        // 切换状态：1 -> 0 或 0 -> 1
        dish.setStatus(dish.getStatus() == 1 ? 0 : 1);
        dishMapper.updateById(dish);  // 更新菜品状态
    }

    /**
     * 删除菜品
     *
     * @param id 菜品ID
     */
    @Override
    public void deleteDish(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }

        dish.setStatus(-1);
        dishMapper.updateById(dish);
    }
}
