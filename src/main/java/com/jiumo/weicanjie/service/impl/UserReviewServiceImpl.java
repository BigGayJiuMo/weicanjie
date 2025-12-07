package com.jiumo.weicanjie.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.mapper.RestaurantMapper;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserReviewServiceImpl implements UserReviewService {

    @Autowired
    UserReviewMapper userReviewMapper;

    @Autowired
    RestaurantMapper restaurantMapper;

    @Override
    public List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId) {
        List<Map<String, Object>> list = userReviewMapper.selectReviewsByRestaurantId(restaurantId);
        return handleList(list);
    }

    @Override
    public List<Map<String, Object>> getAdminReviews(Long restaurantId, Integer reviewStatus) {
        List<Map<String, Object>> list = userReviewMapper.adminSelectReviews(restaurantId, reviewStatus);
        return handleList(list);
    }

    private List<Map<String, Object>> handleList(List<Map<String, Object>> list) {

        for (Map<String, Object> item : list) {

            String json = (String) item.get("image_urls");
            if (json != null && !json.isEmpty())
                item.put("images", JSONArray.parseArray(json, String.class));
            else
                item.put("images", Collections.emptyList());

            Integer isAnon = (Integer) item.get("is_anonymous");
            if (isAnon != null && isAnon == 1) {
                item.put("username", "匿名用户");
                item.put("avatar", "/images/default-avatar.png");
            }

            item.put("reviewStatus", item.get("review_status"));
            item.put("rejectReason", item.get("reject_reason"));
        }

        return list;
    }


    /** 更新餐厅评分（只统计审核通过的） */
    @Override
    public void updateRestaurantRating(Long restaurantId) {
        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        double avg = reviews.stream()
                .mapToInt(v -> (Integer) v.get("rating"))
                .average()
                .orElse(0);

        Restaurant r = new Restaurant();
        r.setId(restaurantId);
        r.setAvgRating(avg == 0 ? null : avg);

        restaurantMapper.updateById(r);
    }

    @Override
    public double calculateAvgRating(Long restaurantId) {
        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        if (reviews.isEmpty()) return -1;

        return reviews.stream().mapToInt(v -> (Integer) v.get("rating")).average().orElse(0);
    }

    @Override
    public void replyReview(Long reviewId, String replyContent) {
        userReviewMapper.replyReview(reviewId, replyContent);
    }
}
