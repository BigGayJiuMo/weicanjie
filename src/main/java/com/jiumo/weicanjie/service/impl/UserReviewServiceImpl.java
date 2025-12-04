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

            // ⭐ 处理图片 JSON
            String imgJson = (String) item.get("image_urls");
            if (imgJson != null && !imgJson.isEmpty()) {
                item.put("images", JSONArray.parseArray(imgJson, String.class));
            } else {
                item.put("images", Collections.emptyList());
            }

            // ⭐ 匿名用户处理
            Object anon = item.get("is_anonymous");
            if (anon != null && ((Integer) anon) == 1) {
                item.put("username", "匿名用户");
                item.put("avatar", "/images/default-avatar.png");
            } else {
                if (item.get("avatar") == null) {
                    item.put("avatar", "/images/default-avatar.png");
                }
            }

            // ⭐ 新增：餐厅名称 & logo 输出给前端
            item.put("restaurantName", item.get("restaurant_name"));
            item.put("restaurantLogo", item.get("restaurant_logo"));
        }

        return list;
    }


    @Override
    public void updateRestaurantRating(Long restaurantId) {

        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(review -> (Integer) review.get("rating"))
                    .average()
                    .orElse(0);

            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            restaurant.setAvgRating(avgRating);
            restaurantMapper.updateById(restaurant);
        } else {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(restaurantId);
            restaurant.setAvgRating(null);
            restaurantMapper.updateById(restaurant);
        }
    }


    @Override
    public double calculateAvgRating(Long restaurantId) {

        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        if (reviews.isEmpty()) {
            return -1;
        }

        return reviews.stream()
                .mapToInt(review -> (Integer) review.get("rating"))
                .average()
                .orElse(0);
    }
}
