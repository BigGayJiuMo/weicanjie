package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.ReviewReport;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.ReviewReportService;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private ReviewReportService reviewReportService;

    /** ============= ① 用户评价审核列表（你原来的接口） ============= */
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId,
                          @RequestParam Integer reviewStatus) {

        return Result.ok(userReviewService.getAdminReviews(restaurantId, reviewStatus));
    }

    /** ============= ② 用户评价审核（你原来的接口） ============= */
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

        review.setStatus(1); // 超管审核后自动显示

        userReviewMapper.updateById(review);

        if (reviewStatus == 1) {
            userReviewService.updateRestaurantRating(review.getRestaurantId());
        }

        return Result.success("审核完成");
    }

    /** ③-1 举报列表（ReviewReport + UserReview 信息） */
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


    /** ③-2 举报审核（通过 / 驳回，并可隐藏评价） */
    @PostMapping("/report/audit")
    @Transactional
    public Result<?> auditReport(
            @RequestParam Long id,
            @RequestParam Integer status,     // 1通过 2驳回
            @RequestParam(required = false) String resultComment,
            @RequestParam(required = false) Integer reviewAction, // 1隐藏评价
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

        // 如果举报成立且管理员选择隐藏评价
        if (status == 1 && reviewAction != null && reviewAction == 1) {
            UserReview review = userReviewMapper.selectById(report.getReviewId());
            if (review != null) {
                review.setStatus(0); // 隐藏评价
                userReviewMapper.updateById(review);
            }
        }

        return Result.success("举报审核完成");
    }

}
