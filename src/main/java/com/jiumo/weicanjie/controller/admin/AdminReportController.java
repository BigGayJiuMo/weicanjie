package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取统计报表数据
     * @param startDate 开始日期（可选，格式：yyyy-MM-dd）
     * @param endDate 结束日期（可选，格式：yyyy-MM-dd）
     * @param request HttpServletRequest对象，用于获取当前用户的角色信息
     * @return 返回指定日期范围内的统计报表数据
     * @note 该接口仅允许管理员角色（super）访问
     */
    @GetMapping("/data")
    public Result<?> getReportData(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {

        // 校验当前用户角色，只有管理员角色才能查看报表
        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限");
        }

        // 获取并返回报表数据
        List<?> reportData = reportService.generateReport(startDate, endDate);

        return Result.ok(reportData);
    }
}
