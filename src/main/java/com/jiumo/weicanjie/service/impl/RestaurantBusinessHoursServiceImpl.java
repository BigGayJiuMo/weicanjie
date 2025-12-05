package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantBusinessHours;
import com.jiumo.weicanjie.mapper.RestaurantBusinessHoursMapper;
import com.jiumo.weicanjie.service.RestaurantBusinessHoursService;
import org.springframework.stereotype.Service;

@Service
public class RestaurantBusinessHoursServiceImpl
        extends ServiceImpl<RestaurantBusinessHoursMapper, RestaurantBusinessHours>
        implements RestaurantBusinessHoursService {
}
