package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.mapper.RestaurantMapper;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserReviewServiceImpl implements UserReviewService {

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Override
    public List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId) {
        List<Map<String, Object>> list = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        for (Map<String, Object> item : list) {

            // JSON 图片 → List<String>
            String imgJson = (String) item.get("image_urls");
            if (imgJson != null && !imgJson.isEmpty()) {
                // 使用 FastJSON 的 parseArray 方法
                item.put("images", JSONArray.parseArray(imgJson, String.class));
            } else {
                item.put("images", Collections.emptyList());
            }

            // 默认头像
            if (item.get("avatar") == null) {
                item.put("avatar", "/images/default-avatar.png");
            }
        }

        return list;
    }
    public void updateRestaurantRating(Long restaurantId) {
        // 获取所有评价
        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        if (!reviews.isEmpty()) {
            // 计算平均评分
            double avgRating = reviews.stream()
                    .mapToInt(review -> (Integer) review.get("rating"))
                    .average()
                    .orElse(0);

            // 更新餐厅的 avgRating
            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            restaurant.setAvgRating(avgRating); // 设置新的评分
            restaurantMapper.updateById(restaurant);
        } else {
            // 如果没有评论，设置 avgRating 为 null
            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            restaurant.setAvgRating(null); // 设置为 null
            restaurantMapper.updateById(restaurant);
        }
    }

    @Override
    public double calculateAvgRating(Long restaurantId) {
        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        if (reviews.isEmpty()) {
            return -1; // 如果没有评价，返回-1表示没有评分
        }

        // 计算评分的平均值
        double avgRating = reviews.stream()
                .mapToInt(review -> (Integer) review.get("rating"))
                .average()
                .orElse(0);

        return avgRating;
    }
}

