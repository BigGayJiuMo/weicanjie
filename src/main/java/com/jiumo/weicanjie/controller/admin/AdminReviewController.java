package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/admin/review")
public class AdminReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private UserReviewMapper userReviewMapper;

    /** 超管审核列表 */
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId,
                          @RequestParam Integer reviewStatus) {

        return Result.ok(userReviewService.getAdminReviews(restaurantId, reviewStatus));
    }

    /** 超管审核操作 */
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
        review.setStatus(1);

        userReviewMapper.updateById(review);

        if (reviewStatus == 1)
            userReviewService.updateRestaurantRating(review.getRestaurantId());

        return Result.success("审核完成");
    }
}
