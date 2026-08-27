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

/**
 * 用户评价举报服务实现类，提供对用户评价举报的具体操作。
 * 包括添加举报记录、查询餐厅ID以及管理员查看举报列表等功能。
 */
@Service
public class ReviewReportServiceImpl extends ServiceImpl<ReviewReportMapper, ReviewReport>
        implements ReviewReportService {

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private ReviewReportMapper reviewReportMapper;

    /**
     * 添加举报记录。
     * 将举报状态设为待审核，并记录当前时间。
     *
     * @param report 举报信息对象，包含评价ID、举报原因等详细信息
     */
    @Override
    public void addReport(ReviewReport report) {

        // 设置举报状态为待审核
        report.setStatus(0);
        // 设置举报创建时间为当前时间
        report.setCreatedTime(LocalDateTime.now());

        // 保存举报记录
        this.save(report);
    }

    /**
     * 根据评价ID获取对应的餐厅ID。
     *
     * @param reviewId 评价ID
     * @return 返回餐厅ID，如果评价不存在，则返回null
     */
    @Override
    public Long getRestaurantIdByReviewId(Long reviewId) {
        // 获取评价信息
        UserReview review = userReviewMapper.selectById(reviewId);
        // 如果找到评价，返回关联的餐厅ID，否则返回null
        return review != null ? review.getRestaurantId() : null;
    }

    /**
     * 获取管理员查看的举报列表。
     * 支持根据举报状态和餐厅ID进行筛选。
     *
     * @param status 举报状态（0:待处理, 1:已处理, 2:已驳回）
     * @param restaurantId 餐厅ID（可选，若不提供则查询所有餐厅的举报）
     * @return 返回符合条件的举报列表，每个举报包括举报原因、相关评价信息等
     */
    @Override
    public List<Map<String, Object>> adminList(Integer status, Long restaurantId) {
        // 获取符合条件的举报列表
        return reviewReportMapper.adminList(status, restaurantId);
    }

    @Override
    public void deleteByReviewId(Long reviewId) {
        reviewReportMapper.deleteByReviewId(reviewId);
    }
}
