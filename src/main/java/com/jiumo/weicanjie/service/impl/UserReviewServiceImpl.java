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
    private UserReviewMapper userReviewMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

    /**
     * 根据餐厅ID获取该餐厅的所有用户评价
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅的所有用户评价（包括图片链接、用户名等信息）
     */
    @Override
    public List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId) {
        List<Map<String, Object>> list = userReviewMapper.selectReviewsByRestaurantId(restaurantId);
        return handleList(list);
    }

    /**
     * 获取管理员的评价列表
     *
     * @param restaurantId 餐厅ID
     * @param reviewStatus 评价状态（用于过滤特定状态的评价）
     * @return 返回符合条件的管理员可见评价列表
     */
    @Override
    public List<Map<String, Object>> getAdminReviews(Long restaurantId, Integer reviewStatus) {
        List<Map<String, Object>> list = userReviewMapper.adminSelectReviewsFlexible(restaurantId, reviewStatus);
        return handleList(list);
    }

    /**
     * 处理评价列表，解析图片URL和处理匿名用户信息
     *
     * @param list 需要处理的评价列表
     * @return 处理后的评价列表，包含图片URL和匿名用户信息
     */
    private List<Map<String, Object>> handleList(List<Map<String, Object>> list) {
        for (Map<String, Object> item : list) {

            // 解析评价图片URL字段，转换为List
            String json = (String) item.get("image_urls");
            if (json != null && !json.isEmpty()) {
                item.put("images", JSONArray.parseArray(json, String.class));
            } else {
                item.put("images", Collections.emptyList());
            }

            // 处理匿名用户
            Integer isAnon = (Integer) item.get("is_anonymous");
            if (isAnon != null && isAnon == 1) {
                item.put("username", "匿名用户");
                item.put("avatar", "/images/default-avatar.png");
            }

            // 添加其他的评价状态信息
            item.put("reviewStatus", item.get("review_status"));
            item.put("rejectReason", item.get("reject_reason"));
        }

        return list;
    }

    /**
     * 更新餐厅的评分（仅统计审核通过的评价）
     *
     * @param restaurantId 餐厅ID
     */
    @Override
    public void updateRestaurantRating(Long restaurantId) {
        // 只统计：review_status = 1（审核通过） 且 status = 0（正常）的评价
        List<Map<String, Object>> reviews = userReviewMapper.selectValidReviewsForRating(restaurantId);

        // 没评价 → 设置为 0 (或 NULL，看你系统需求)
        if (reviews == null || reviews.isEmpty()) {
            Restaurant r = new Restaurant();
            r.setId(restaurantId);
            r.setAvgRating(0.0);   // ⭐如果你想显示 “暂无评分”，可以改成 null
            restaurantMapper.updateById(r);
            return;
        }

        // 计算平均评分（包含 null 安全处理）
        double avg = reviews.stream()
                .mapToDouble(v -> {
                    Object val = v.get("rating"); // 你的字段就是 rating（正确）
                    if (val == null) return 0;     // 空值安全处理
                    return Double.parseDouble(val.toString());
                })
                .average()
                .orElse(0);

        // 写回数据库
        Restaurant r = new Restaurant();
        r.setId(restaurantId);
        r.setAvgRating(avg);
        restaurantMapper.updateById(r);
    }

    /**
     * 计算餐厅的平均评分
     *
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的平均评分，若无评分则返回-1
     */
    @Override
    public double calculateAvgRating(Long restaurantId) {
        List<Map<String, Object>> reviews = userReviewMapper.selectReviewsByRestaurantId(restaurantId);

        // 若没有评价，返回-1
        if (reviews.isEmpty()) return -1;

        return reviews.stream()
                .mapToInt(v -> (Integer) v.get("rating"))
                .average()
                .orElse(0);
    }

    /**
     * 回复用户的评价
     *
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     */
    @Override
    public void replyReview(Long reviewId, String replyContent) {
        // 执行回复操作
        userReviewMapper.replyReview(reviewId, replyContent);
    }
}
