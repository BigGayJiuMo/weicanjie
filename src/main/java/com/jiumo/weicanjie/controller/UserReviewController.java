package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.mapper.UserStatsMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class UserReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @Autowired
    private UserStatsService statsService;

    @PostMapping("/add")
    public Result<?> addReview(@RequestBody UserReview userReview) {
        try {
            if (userReview.getUserId() == null || userReview.getRestaurantId() == null) {
                return Result.error("参数不完整");
            }

            // 插入评价
            userReviewMapper.insert(userReview);

            // 更新餐厅平均分
            userReviewService.updateRestaurantRating(userReview.getRestaurantId());

            // 用户评价数 +1
            statsService.incrementReviewCount(userReview.getUserId());

            return Result.success("评价提交成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("评价提交失败");
        }
    }

    @PostMapping("/delete/{id}")
    public Result<?> deleteReview(@PathVariable Long id) {
        try {
            // 查询评价（为了拿 userId 和 restaurantId）
            UserReview review = userReviewMapper.selectById(id);
            if (review == null) {
                return Result.error("评价不存在");
            }

            Long userId = review.getUserId();
            Long restaurantId = review.getRestaurantId();

            // 删除评价
            userReviewMapper.deleteById(id);

            //  更新餐厅评分
            userReviewService.updateRestaurantRating(restaurantId);

            //  用户评价数量 -1
            statsService.decrementReviewCount(userId);

            return Result.success("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId) {
        return Result.ok(userReviewService.getReviewsByRestaurantId(restaurantId));
    }

    @GetMapping("/userReviews")
    public Result<?> getByUser(@RequestParam Long userId) {
        return Result.ok(userReviewMapper.selectByUserId(userId));
    }
}
