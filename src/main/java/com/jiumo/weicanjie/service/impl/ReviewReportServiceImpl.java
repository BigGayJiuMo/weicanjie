package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.ReviewReport;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.ReviewReportMapper;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.ReviewReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReviewReportServiceImpl extends ServiceImpl<ReviewReportMapper, ReviewReport>
        implements ReviewReportService {

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private ReviewReportMapper reviewReportMapper;

    @Override
    public void addReport(ReviewReport report) {

        report.setStatus(0); // 待审核
        report.setCreatedTime(LocalDateTime.now());

        this.save(report);
    }

    @Override
    public Long getRestaurantIdByReviewId(Long reviewId) {
        UserReview review = userReviewMapper.selectById(reviewId);
        return review != null ? review.getRestaurantId() : null;
    }

    @Override
    public List<Map<String, Object>> adminList(Integer status, Long restaurantId) {
        return reviewReportMapper.adminList(status, restaurantId);
    }
}
