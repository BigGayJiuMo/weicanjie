package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserReview;
import com.jiumo.weicanjie.mapper.UserReviewMapper;
import com.jiumo.weicanjie.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class UserReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @Autowired
    private UserReviewMapper userReviewMapper;

    @PostMapping("/add")
    public Result<?> addReview(@RequestBody UserReview userReview) {
        try {
            if (userReview.getUserId() == null || userReview.getRestaurantId() == null) {
                return Result.error("参数不完整");
            }

            userReviewMapper.insert(userReview);

            userReviewService.updateRestaurantRating(userReview.getRestaurantId());

            return Result.success("评价提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("评价提交失败");
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
