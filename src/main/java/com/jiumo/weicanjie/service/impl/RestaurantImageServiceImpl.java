package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.mapper.RestaurantImageMapper;
import com.jiumo.weicanjie.service.RestaurantImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantImageServiceImpl
        extends ServiceImpl<RestaurantImageMapper, RestaurantImage>
        implements RestaurantImageService {

    @Autowired
    private RestaurantImageMapper restaurantImageMapper;

    @Override
    public boolean addImage(RestaurantImage img) {

        long count = restaurantImageMapper.selectCount(
                new QueryWrapper<RestaurantImage>().eq("restaurant_id", img.getRestaurantId())
        );

        if (count >= 3) {
            throw new RuntimeException("单个餐厅最多只能上传 3 张图片");
        }

        return restaurantImageMapper.insert(img) > 0;
    }
}
