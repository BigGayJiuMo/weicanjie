package com.jiumo.weicanjie.controller;

import com.alibaba.fastjson.JSON;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.ReviewReport;
import com.jiumo.weicanjie.service.ReviewReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review/report")
public class ReviewReportController {

    @Autowired
    private ReviewReportService reviewReportService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody Map<String, Object> body) {

        System.out.println("收到的 body = " + body);

        ReviewReport report = new ReviewReport();

        // 基本字段
        report.setReviewId(Long.valueOf(body.get("reviewId").toString()));
        report.setReporterId(Long.valueOf(body.get("reporterId").toString()));
        report.setReason((String) body.get("reason"));
        report.setDetail((String) body.get("detail"));

        // 处理 images 数组 → JSON 字符串
        List<String> imgList = (List<String>) body.get("images");
        report.setImages(JSON.toJSONString(imgList));

        // 自动查所属餐厅
        Long restaurantId = reviewReportService.getRestaurantIdByReviewId(report.getReviewId());
        if (restaurantId == null) {
            return Result.error("评价不存在");
        }
        report.setRestaurantId(restaurantId);

        reviewReportService.addReport(report);
        return Result.success("举报成功");
    }
}
