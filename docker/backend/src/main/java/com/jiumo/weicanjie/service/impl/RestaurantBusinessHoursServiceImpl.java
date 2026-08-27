package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantBusinessHours;
import com.jiumo.weicanjie.mapper.RestaurantBusinessHoursMapper;
import com.jiumo.weicanjie.service.RestaurantBusinessHoursService;
import org.springframework.stereotype.Service;

/**
 * 餐厅营业时间服务实现类。
 * 该类继承自 MyBatis-Plus 提供的 ServiceImpl 类，自动实现餐厅营业时间（RestaurantBusinessHours）实体的增删改查 (CRUD) 操作。
 * <p>
 * 通过实现 {@link RestaurantBusinessHoursService} 接口，该类提供了对餐厅营业时间数据的操作。
 * </p>
 */
@Service
public class RestaurantBusinessHoursServiceImpl
        extends ServiceImpl<RestaurantBusinessHoursMapper, RestaurantBusinessHours>
        implements RestaurantBusinessHoursService {
    // 当前类通过继承 ServiceImpl，实现了对餐厅营业时间（RestaurantBusinessHours）数据的操作。
}
