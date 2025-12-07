package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.service.UserReviewService;
import com.jiumo.weicanjie.service.UserStatsService;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/review")
public class UserReviewController {

    @Autowired
    UserReviewService userReviewService;

    @Autowired
    UserReviewMapper userReviewMapper;

    @Autowired
    UserStatsService statsService;

    /** 用户提交评价 */
    @PostMapping("/add")
    public Result<?> addReview(@RequestBody UserReview review) {
        if (review.getUserId() == null || review.getRestaurantId() == null)
            return Result.error("参数不完整");

        review.setReviewStatus(0); // 待审核
        userReviewMapper.insert(review);

        statsService.incrementReviewCount(review.getUserId());

        return Result.success("评价已提交，等待审核");
    }

    /** 餐厅评价列表 */
    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId) {
        return Result.success(userReviewService.getReviewsByRestaurantId(restaurantId));
    }

    /** 删除评价 */
    @PostMapping("/delete/{id}")
    public Result<?> deleteReview(@PathVariable Long id) {
        UserReview review = userReviewMapper.selectById(id);
        if (review == null) return Result.error("评价不存在");

        userReviewMapper.deleteById(id);

        userReviewService.updateRestaurantRating(review.getRestaurantId());
        statsService.decrementReviewCount(review.getUserId());

        return Result.success("删除成功");
    }

    /** 用户自己的评价 */
    @GetMapping("/userReviews")
    public Result<?> getByUser(@RequestParam Long userId) {
        return Result.ok(userReviewMapper.selectByUserId(userId));
    }


}
