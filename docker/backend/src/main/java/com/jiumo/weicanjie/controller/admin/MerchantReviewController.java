package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商家评价管理控制器
 * 该控制器提供商家对用户评价的回复和删除功能。商家可以对自己餐厅的评价进行回复，
 * 并有权限删除回复。超管有权限管理所有餐厅的评价回复。
 */
@RestController
@RequestMapping("/admin/merchant/review")
public class MerchantReviewController {

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private UserReviewService userReviewService;

    /**
     * 商家回复用户评价
     * 商家只能回复自己餐厅的评价，超管可以回复所有餐厅的评价。
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     * @param request 请求对象，用于获取当前用户角色和餐厅ID
     * @return 返回回复结果
     */
    @PostMapping("/reply")
    public Result<?> reply(@RequestParam Long reviewId,
                           @RequestParam String replyContent,
                           HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long restaurantId = (Long) request.getAttribute("restaurantId");

        // 角色检查：只有商家和超管可以回复
        if (!"merchant".equals(role) && !"super".equals(role)) {
            return Result.error("无权限");
        }

        // 查找评价
        UserReview review = userReviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");

        // 商家只能回复自己餐厅的评价
        if ("merchant".equals(role) && !review.getRestaurantId().equals(restaurantId)) {
            return Result.error("不能回复别家餐厅的评价");
        }

        // 执行回复操作
        userReviewMapper.replyReview(reviewId, replyContent);
        return Result.success("回复成功");
    }

    /**
     * 删除商家对评价的回复
     * 商家只能删除自己餐厅的评价回复，超管可以删除所有餐厅的回复。
     * @param reviewId 评价ID
     * @param request 请求对象，用于获取当前用户角色和餐厅ID
     * @return 返回删除结果
     */
    @PostMapping("/reply/delete")
    public Result<?> deleteReply(@RequestParam Long reviewId,
                                 HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long restaurantId = (Long) request.getAttribute("restaurantId");

        // 角色检查：只有商家和超管可以删除
        if (!"merchant".equals(role) && !"super".equals(role)) {
            return Result.error("无权限");
        }

        // 查找评价
        UserReview review = userReviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");

        // 商家只能删除自己餐厅的回复
        if ("merchant".equals(role) && !review.getRestaurantId().equals(restaurantId)) {
            return Result.error("不能删除别家餐厅的回复");
        }

        // 执行删除操作
        userReviewMapper.deleteReply(reviewId);
        return Result.success("删除成功");
    }
}
