package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.ReviewReport;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.ReviewReportService;
import com.jiumo.weicanjie.service.UserReviewService;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台管理用户评价相关功能的控制器
 */
@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private ReviewReportService reviewReportService;

    @Autowired
    private UserStatsService userStatsService;

    /**
     * 获取用户评价审核列表
     * @param restaurantId 餐厅ID（可选）
     * @param reviewStatus 评价状态（-1表示所有状态，其他数字表示特定状态）
     * @return 返回符合条件的评价列表
     * @note 超级管理员可以查看所有，商家只能查看自己的餐厅评价
     */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(defaultValue = "-1") Integer reviewStatus
    ) {
        return Result.ok(
                userReviewService.getAdminReviews(restaurantId, reviewStatus)
        );
    }

    /**
     * 审核用户评价
     * @param reviewId 评价ID
     * @param reviewStatus 评价状态（1=通过，0=拒绝）
     * @param rejectReason 拒绝原因（如果有）
     * @param request HTTP请求，获取角色权限
     * @return 审核结果
     * @note 只有超级管理员可以审核用户评价，审核后自动更新餐厅评分（如通过审核）
     */
    @PostMapping("/audit")
    @Transactional
    public Result<?> auditReview(
            @RequestParam Long reviewId,
            @RequestParam Integer reviewStatus,
            @RequestParam(required = false) String rejectReason,
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) return Result.error("无权限");

        UserReview review = userReviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");

        review.setReviewStatus(reviewStatus);
        review.setRejectReason(rejectReason);
        review.setReviewTime(new Date());

        review.setStatus(1); // 超级管理员审核通过后自动显示

        userReviewMapper.updateById(review);

        // 如果审核通过，更新餐厅评分
        if (reviewStatus == 1) {
            userReviewService.updateRestaurantRating(review.getRestaurantId());
        }

        return Result.success("审核完成");
    }

    /**
     * 获取举报列表
     * @param restaurantId 餐厅ID（可选）
     * @param status 举报状态（可选，默认查询所有）
     * @param request HTTP请求，获取角色权限
     * @return 返回举报列表
     * @note 仅超级管理员有权限查看举报信息
     */
    @GetMapping("/report/list")
    public Result<?> getReportList(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) return Result.error("无权限");

        List<Map<String, Object>> data =
                reviewReportService.adminList(status, restaurantId);

        return Result.success(data);
    }

    /**
     * 审核举报
     * @param id 举报ID
     * @param status 举报处理状态（1=通过，2=驳回）
     * @param resultComment 审核结果评论
     * @param reviewAction 审核动作（1=隐藏评价，其他=不操作）
     * @param request HTTP请求，获取角色权限
     * @return 审核结果
     * @note 只有超级管理员可以进行举报审核，审核后根据状态进行处理
     */
    @PostMapping("/report/audit")
    @Transactional
    public Result<?> auditReport(
            @RequestParam Long id,
            @RequestParam Integer status,     // 1:通过 2:驳回
            @RequestParam(required = false) String resultComment,
            @RequestParam(required = false) Integer reviewAction, // 1:隐藏评价
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) return Result.error("无权限");

        ReviewReport report = reviewReportService.getById(id);
        if (report == null) return Result.error("举报不存在");

        report.setStatus(status);
        report.setResultComment(resultComment);
        report.setReviewAction(reviewAction);
        report.setUpdatedTime(LocalDateTime.now());

        reviewReportService.updateById(report);

        // 如果举报通过且选择隐藏评价
        if (status == 1 && reviewAction != null && reviewAction == 1) {
            UserReview review = userReviewMapper.selectById(report.getReviewId());
            if (review != null) {
                review.setStatus(0); // 隐藏评价
                userReviewMapper.updateById(review);
            }
        }

        return Result.success("举报审核完成");
    }

    /**
     * 超级管理员删除评价
     */
    @PostMapping("/delete/{id}")
    @Transactional
    public Result<?> adminDeleteReview(
            @PathVariable Long id,
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) return Result.error("无权限");

        // 查评价是否存在
        UserReview review = userReviewMapper.selectById(id);
        if (review == null) return Result.error("评价不存在");

        // ① 删除举报记录（review_report）
        reviewReportService.deleteByReviewId(id);

        // ② 删除评价
        userReviewMapper.deleteById(id);

        // ③ 更新餐厅评分
        userReviewService.updateRestaurantRating(review.getRestaurantId());

        // ④ 更新用户统计
        userStatsService.decrementReviewCount(review.getUserId());

        return Result.success("删除成功");
    }
}
