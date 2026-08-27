package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.RestaurantBusinessHours;

/**
 * 餐厅营业时间服务接口。
 * 该接口继承了 IService 接口，提供餐厅营业时间的基本增删改查 (CRUD) 操作。
 * <p>
 * 通过继承 MyBatis-Plus 的 IService 接口，实现对餐厅营业时间实体的自动操作。
 */
public interface RestaurantBusinessHoursService extends IService<RestaurantBusinessHours> {
    // 该接口继承自 MyBatis-Plus 的 IService 接口，自动提供餐厅营业时间的基本 CRUD 操作
}
