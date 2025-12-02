package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.RestaurantImage;

public interface RestaurantImageService extends IService<RestaurantImage> {

    boolean addImage(RestaurantImage img);

}
