package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.RestaurantCategory;
import com.jiumo.weicanjie.service.RestaurantCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class RestaurantCategoryController {

    @Autowired
    private RestaurantCategoryService service;

    @GetMapping("/list")
    public Result<List<RestaurantCategory>> list() {
        return Result.success(service.list());
    }
}