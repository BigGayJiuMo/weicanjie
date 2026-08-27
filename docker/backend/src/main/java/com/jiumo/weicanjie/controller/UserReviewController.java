package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.service.UserReviewService;
import com.jiumo.weicanjie.service.UserStatsService;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户评价管理控制器
 * 该控制器提供用户评价的管理功能，包括提交评价、查看评价、删除评价和获取用户的所有评价。
 */
@RestController
@RequestMapping("/review")
public class UserReviewController {

    @Autowired
    UserReviewService userReviewService;

    @Autowired
    UserReviewMapper userReviewMapper;

    @Autowired
    UserStatsService statsService;

    /**
     * 用户提交评价
     *
     * 该接口允许用户对餐厅进行评价，提交的评价会进入待审核状态，管理员审核通过后才会显示。
     *
     * @param review 用户评价对象，包含用户ID、餐厅ID、评价内容等信息
     * @return 返回提交评价的结果，成功时返回“评价已提交，等待审核”
     */
    @PostMapping("/add")
    public Result<?> addReview(@RequestBody UserReview review) {
        // 校验用户ID和餐厅ID是否提供
        if (review.getUserId() == null || review.getRestaurantId() == null)
            return Result.error("参数不完整");

        review.setReviewStatus(0); // 设置评价状态为待审核
        userReviewMapper.insert(review); // 保存评价到数据库

        // 更新用户的评价统计数据
        statsService.incrementReviewCount(review.getUserId());

        return Result.success("评价已提交，等待审核");
    }

    /**
     * 获取餐厅的评价列表
     *
     * 该接口用于获取指定餐厅的所有评价列表，供餐厅查看其顾客的评价。
     *
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的所有评价列表
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId) {
        return Result.success(userReviewService.getReviewsByRestaurantId(restaurantId));
    }

    /**
     * 删除评价
     *
     * 该接口允许删除指定ID的用户评价。如果评价存在并且删除成功，会更新餐厅的评分和用户的评价计数。
     *
     * @param id 评价ID
     * @return 返回删除操作的结果，成功时返回“删除成功”
     */
    @PostMapping("/delete/{id}")
    public Result<?> deleteReview(@PathVariable Long id) {

        // 查找评价
        UserReview review = userReviewMapper.selectById(id);
        if (review == null) return Result.error("评价不存在");

        // 先删除与该评价相关的所有举报记录（review_report）
        userReviewMapper.deleteReportsByReviewId(id);

        // 删除评价
        userReviewMapper.deleteById(id);

        // 更新餐厅评分
        userReviewService.updateRestaurantRating(review.getRestaurantId());

        // 更新用户评价计数
        statsService.decrementReviewCount(review.getUserId());

        return Result.success("删除成功");
    }

    /**
     * 获取用户自己的评价
     *
     * 该接口用于获取指定用户的所有评价记录，供用户查看自己对餐厅的评价历史。
     *
     * @param userId 用户ID
     * @return 返回用户的所有评价记录
     */
    @GetMapping("/userReviews")
    public Result<?> getByUser(@RequestParam Long userId) {
        return Result.ok(userReviewMapper.selectByUserId(userId));
    }
}
