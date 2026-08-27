package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.ReviewReport;

import java.util.List;
import java.util.Map;

/**
 * 用户评价举报服务接口，提供对用户评价举报的相关操作。
 * 包括添加举报、查询餐厅ID以及管理员查看举报列表等功能。
 */
public interface ReviewReportService extends IService<ReviewReport> {

    /**
     * 提交举报，记录用户对指定评价的举报信息。
     *
     * @param report 举报信息对象，包含评价ID、举报原因等
     */
    void addReport(ReviewReport report);

    /**
     * 根据评价ID获取相关的餐厅ID。
     *
     * @param reviewId 评价ID
     * @return 返回餐厅ID
     */
    Long getRestaurantIdByReviewId(Long reviewId);

    /**
     * 获取管理员查看的举报列表。
     * 支持根据举报状态和餐厅ID进行筛选。
     *
     * @param status 举报状态（0：待处理，1：已处理，2：已驳回）
     * @param restaurantId 餐厅ID（可选，若不提供则查询所有餐厅的举报）
     * @return 返回符合条件的举报列表，每个举报包括举报原因、相关评价信息等
     */
    List<Map<String, Object>> adminList(Integer status, Long restaurantId);

    void deleteByReviewId(Long reviewId);
}
