package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Favorite;

import java.util.List;
import java.util.Map;

public interface FavoriteService extends IService<Favorite> {

    Result<String> addRestaurantFavorite(Long userId, Long restaurantId);

    Result<String> removeRestaurantFavorite(Long userId, Long restaurantId);

    boolean isRestaurantFavorite(Long userId, Long restaurantId);

    Integer getRestaurantFavoriteCount(Long userId);

    List<Map<String, Object>> getFavoriteRestaurantList(Long userId);
}
