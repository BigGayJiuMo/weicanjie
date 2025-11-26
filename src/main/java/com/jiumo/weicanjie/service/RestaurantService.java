// RestaurantService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

public interface RestaurantService extends IService<Restaurant> {

    Result<List<Restaurant>> getActiveRestaurants();

    Result<Restaurant> getRestaurantDetail(Long id);

    Result<List<Restaurant>> getAllRestaurants();
}