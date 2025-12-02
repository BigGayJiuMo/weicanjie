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

    private UserReviewMapper userReviewMapper;
    @PostMapping("/add")
    public Result<?> addReview(@RequestBody UserReview userReview) {
        // 假设已处理好用户提交的评价数据
        try {
            // 处理评价提交
            // 保存评价数据
            userReviewMapper.insert(userReview);

            // 更新餐厅的评分
            userReviewService.updateRestaurantRating(userReview.getRestaurantId());

            return Result.success("评价提交成功");
        } catch (Exception e) {
            return Result.error("评价提交失败");
        }
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam Long restaurantId) {
        return Result.ok(userReviewService.getReviewsByRestaurantId(restaurantId));
    }


}
