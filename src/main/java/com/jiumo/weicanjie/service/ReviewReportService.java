package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.ReviewReport;

import java.util.List;
import java.util.Map;

public interface ReviewReportService extends IService<ReviewReport> {

    void addReport(ReviewReport report);

    Long getRestaurantIdByReviewId(Long reviewId);

    List<Map<String, Object>> adminList(Integer status, Long restaurantId);
}
