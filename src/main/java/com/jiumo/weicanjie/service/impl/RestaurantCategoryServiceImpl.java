package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantCategory;
import com.jiumo.weicanjie.mapper.RestaurantCategoryMapper;
import com.jiumo.weicanjie.service.RestaurantCategoryService;
import org.springframework.stereotype.Service;

@Service
public class RestaurantCategoryServiceImpl extends ServiceImpl<RestaurantCategoryMapper, RestaurantCategory>
        implements RestaurantCategoryService {}