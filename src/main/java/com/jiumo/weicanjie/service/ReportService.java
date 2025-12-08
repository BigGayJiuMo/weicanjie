package com.jiumo.weicanjie.service;

import java.time.LocalDate;
import java.util.List;

/**
 * 报表服务接口。
 * <p>
 * 该接口定义了生成统计报表的方法。实现该接口的类应提供生成指定时间范围内的统计报表的功能。
 * </p>
 */
public interface ReportService {

    /**
     * 生成统计报表。
     * <p>
     * 该方法接受一个起始日期和结束日期，生成覆盖该时间范围内的统计报表数据。
     * 报表的内容和格式将根据具体实现而有所不同。
     * </p>
     *
     * @param startDate 起始日期，定义报表统计的开始日期
     * @param endDate 结束日期，定义报表统计的结束日期
     * @return 返回包含统计数据的列表，具体内容取决于实现
     */
    List<?> generateReport(LocalDate startDate, LocalDate endDate);
}
