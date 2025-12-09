package com.jiumo.weicanjie.controller;

import com.alibaba.fastjson.JSON;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.ReviewReport;
import com.jiumo.weicanjie.service.ReviewReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论举报管理控制器
 * 该控制器提供评论举报的功能，允许用户对不当评论进行举报。
 */
@RestController
@RequestMapping("/review/report")
public class ReviewReportController {

    @Autowired
    private ReviewReportService reviewReportService;

    /**
     * 添加评论举报
     *
     * 该接口允许用户对特定评论进行举报，并提供举报的原因、详细描述和相关图片。
     *
     * @param body 请求体，包含举报的基本信息，如评论ID、举报者ID、举报原因、详细说明和相关图片
     * @return 返回举报操作的结果，成功或失败
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Map<String, Object> body) {

        System.out.println("收到的 body = " + body);

        // 创建举报对象
        ReviewReport report = new ReviewReport();

        // 填充举报对象的基本字段
        report.setReviewId(Long.valueOf(body.get("reviewId").toString()));  // 设置评论ID
        report.setReporterId(Long.valueOf(body.get("reporterId").toString()));  // 设置举报者ID
        report.setReason((String) body.get("reason"));  // 设置举报原因
        report.setDetail((String) body.get("detail"));  // 设置举报详细信息

        // 处理 images 数组，将其转为 JSON 字符串存储
        List<String> imgList = (List<String>) body.get("images");
        report.setImages(JSON.toJSONString(imgList));

        // 自动查找评论所属的餐厅ID
        Long restaurantId = reviewReportService.getRestaurantIdByReviewId(report.getReviewId());
        if (restaurantId == null) {
            return Result.error("评价不存在");  // 如果评论ID无效，返回错误
        }
        report.setRestaurantId(restaurantId);  // 设置餐厅ID

        // 将举报对象保存到数据库
        reviewReportService.addReport(report);
        return Result.success("举报成功");  // 返回成功响应
    }
}
