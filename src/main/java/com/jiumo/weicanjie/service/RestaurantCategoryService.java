package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.RestaurantCategory;

/**
 * 餐厅分类服务接口。
 * 该接口提供对餐厅分类的服务操作，继承了 IService 接口，包含了基本的 CRUD 操作。
 */
public interface RestaurantCategoryService extends IService<RestaurantCategory> {
    // 该接口继承自 MyBatis-Plus 的 IService 接口，自动提供餐厅分类的基本 CRUD 操作
}
