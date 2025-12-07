package com.jiumo.weicanjie.service;

import java.util.List;
import java.util.Map;

public interface UserReviewService {

    List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId);

    List<Map<String,Object>> getAdminReviews(Long restaurantId, Integer reviewStatus);

    void updateRestaurantRating(Long restaurantId);

    double calculateAvgRating(Long restaurantId);

    void replyReview(Long reviewId, String replyContent);
}
