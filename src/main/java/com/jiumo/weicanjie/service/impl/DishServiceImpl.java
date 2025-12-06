// DishServiceImpl.java
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

    @Override
    public Result<Dish> getById(Long id) {
        try {
            Dish dish = dishMapper.selectById(id);
            if (dish != null) {
                return Result.success(dish);
            } else {
                return Result.error("菜品不存在");
            }
        } catch (Exception e) {
            return Result.error("查询菜品失败: " + e.getMessage());
        }
    }

    @Override
    public Page<Dish> getPage(Long restaurantId, Long categoryId, String keyword, int pageNum, int pageSize) {
        Page<Dish> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getRestaurantId, restaurantId);

        if (categoryId != null) {
            qw.eq(Dish::getCategoryId, categoryId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Dish::getName, keyword);
        }

        qw.orderByAsc(Dish::getCategoryId).orderByAsc(Dish::getId);

        return dishMapper.selectPage(page, qw);
    }

    @Override
    public void addDish(Dish dish) {
        dish.setStatus(1);
        dishMapper.insert(dish);
    }

    @Override
    public void updateDish(Dish dish) {
        dishMapper.updateById(dish);
    }

    @Override
    public void toggleStatus(Long id) {
        Dish d = dishMapper.selectById(id);
        d.setStatus(d.getStatus() == 1 ? 0 : 1);
        dishMapper.updateById(d);
    }

    @Override
    public void deleteDish(Long id) {
        dishMapper.deleteById(id);
    }
}