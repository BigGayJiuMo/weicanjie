// DishServiceImpl.java
package com.jiumo.weicanjie.service.impl;

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
}