package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/merchant/review")
public class MerchantReviewController {

    @Autowired
    UserReviewMapper userReviewMapper;

    @Autowired
    UserReviewService userReviewService;

    /** 商家回复 */
    @PostMapping("/reply")
    public Result<?> reply(@RequestParam Long reviewId,
                           @RequestParam String replyContent,
                           HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long restaurantId = (Long) request.getAttribute("restaurantId");

        if (!"merchant".equals(role) && !"super".equals(role))
            return Result.error("无权限");

        UserReview review = userReviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");

        if ("merchant".equals(role) && !review.getRestaurantId().equals(restaurantId))
            return Result.error("不能回复别家餐厅的评价");

        userReviewMapper.replyReview(reviewId, replyContent);
        return Result.success("回复成功");
    }

    /** 删除回复 */
    @PostMapping("/reply/delete")
    public Result<?> deleteReply(@RequestParam Long reviewId,
                                 HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long restaurantId = (Long) request.getAttribute("restaurantId");

        if (!"merchant".equals(role) && !"super".equals(role))
            return Result.error("无权限");

        UserReview review = userReviewMapper.selectById(reviewId);  
        if (review == null) return Result.error("评价不存在");

        if ("merchant".equals(role) && !review.getRestaurantId().equals(restaurantId))
            return Result.error("不能删除别家餐厅的回复");

        userReviewMapper.deleteReply(reviewId);
        return Result.success("删除成功");
    }
}
