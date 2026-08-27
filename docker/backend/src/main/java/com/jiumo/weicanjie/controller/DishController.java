package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 根据菜品ID获取菜品详情
     *
     * 该接口用于获取指定菜品的详细信息，包括名称、价格、描述等。
     *
     * @param id 菜品ID
     * @return 返回菜品的详细信息
     */
    @GetMapping("/detail/{id}")
    public Result<Dish> getDishDetail(@PathVariable Long id) {
        return dishService.getById(id);
    }
}
