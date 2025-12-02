package com.jiumo.weicanjie.service;

import java.util.List;
import java.util.Map;

public interface UserReviewService {
    List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId);

    void updateRestaurantRating(Long restaurantId);

    double calculateAvgRating(Long restaurantId);
}
