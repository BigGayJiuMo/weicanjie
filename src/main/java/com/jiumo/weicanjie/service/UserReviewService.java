package com.jiumo.weicanjie.service;

import java.util.List;
import java.util.Map;

/**
 * 用户评价服务接口，提供用户评价相关的业务操作。
 * 包含获取用户评价、更新餐厅评分、回复用户评价等功能。
 */
public interface UserReviewService {

    /**
     * 根据餐厅ID获取该餐厅的所有用户评价
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅的所有用户评价
     */
    List<Map<String, Object>> getReviewsByRestaurantId(Long restaurantId);

    /**
     * 获取管理员的用户评价列表
     *
     * @param restaurantId 餐厅ID
     * @param reviewStatus 评价状态
     * @return 返回符合条件的评价列表
     */
    List<Map<String, Object>> getAdminReviews(Long restaurantId, Integer reviewStatus);

    /**
     * 更新餐厅评分（只统计已审核通过的评价）
     *
     * @param restaurantId 餐厅ID
     */
    void updateRestaurantRating(Long restaurantId);

    /**
     * 计算餐厅的平均评分
     *
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的平均评分
     */
    double calculateAvgRating(Long restaurantId);

    /**
     * 回复用户评价
     *
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     */
    void replyReview(Long reviewId, String replyContent);
}
