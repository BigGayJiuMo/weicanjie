package com.jiumo.weicanjie.service;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    /** 生成统计报表 */
    List<?> generateReport(LocalDate startDate, LocalDate endDate);
}
