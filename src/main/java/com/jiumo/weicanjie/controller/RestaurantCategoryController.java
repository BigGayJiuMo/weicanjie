package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.RestaurantCategory;
import com.jiumo.weicanjie.service.RestaurantCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 餐厅分类管理控制器
 * 该控制器提供餐厅分类相关的查询接口，允许获取所有餐厅分类的列表。
 */
@RestController
@RequestMapping("/category")
public class RestaurantCategoryController {

    @Autowired
    private RestaurantCategoryService service;

    /**
     * 获取所有餐厅分类的列表
     *
     * 该接口用于查询系统中所有餐厅分类的信息，返回餐厅分类列表。
     *
     * @return 返回餐厅分类的列表
     */
    @GetMapping("/list")
    public Result<List<RestaurantCategory>> list() {
        return Result.success(service.list());
    }
}
