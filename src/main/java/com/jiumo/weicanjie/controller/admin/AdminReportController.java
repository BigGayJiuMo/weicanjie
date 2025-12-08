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

    /** 获取统计报表数据 */
    @GetMapping("/data")
    public Result<?> getReportData(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            HttpServletRequest request) {

        // 权限校验，只有管理员角色可以查看报表
        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限");
        }

        // 获取报表数据
        List<?> reportData = reportService.generateReport(startDate, endDate);

        return Result.ok(reportData);
    }
}
