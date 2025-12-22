package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.KpiCompareDTO;
import com.jiumo.weicanjie.dto.ReportResponseDTO;
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
     *
     * @param startDate    开始日期（yyyy-MM-dd）
     * @param endDate      结束日期（yyyy-MM-dd）
     * @param restaurantId 餐厅ID（可选，用于联动筛选）
     * @param request      请求对象
     */
    @GetMapping("/data")
    public Result<?> getReportData(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

            @RequestParam(required = false) Long restaurantId,

            @RequestParam(defaultValue = "day") String granularity,

            HttpServletRequest request
    ) {

        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限");
        }

        // ① 图表 & 表格
        List<?> reportData = reportService.generateReport(
                startDate,
                endDate,
                restaurantId,
                granularity
        );

        // ② KPI 环比
        KpiCompareDTO kpiCompare = reportService.getKpiCompare(
                startDate,
                endDate,
                restaurantId,
                granularity
        );

        // ③ 统一返回
        ReportResponseDTO resp = new ReportResponseDTO();
        resp.setData(reportData);
        resp.setKpi(kpiCompare);

        return Result.ok(resp);
    }
}
