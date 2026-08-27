package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantCategory;
import com.jiumo.weicanjie.mapper.RestaurantCategoryMapper;
import com.jiumo.weicanjie.service.RestaurantCategoryService;
import org.springframework.stereotype.Service;

/**
 * 餐厅分类服务实现类。
 * 该类实现了餐厅分类相关的业务操作，如管理餐厅分类信息。
 */
@Service
public class RestaurantCategoryServiceImpl extends ServiceImpl<RestaurantCategoryMapper, RestaurantCategory>
        implements RestaurantCategoryService {
    // 当前类继承自 ServiceImpl，自动提供对餐厅分类表的 CRUD 操作
}
