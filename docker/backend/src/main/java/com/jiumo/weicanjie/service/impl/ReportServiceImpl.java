package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.dto.DishSalesDTO;
import com.jiumo.weicanjie.dto.KpiCompareDTO;
import com.jiumo.weicanjie.dto.KpiData;
import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.util.List;

/**
 * 报表服务实现类。
 * <p>
 * 该类实现了 {@link ReportService} 接口，提供了生成指定日期范围内统计报表的功能。
 * 主要通过查询订单数据来生成报表。
 * </p>
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 生成统计报表。
     * <p>
     * 该方法根据传入的起始日期和结束日期，调用 {@link OrderMapper} 获取相关的订单数据，
     * 生成符合时间范围的统计报表数据。
     * </p>
     *
     * @param startDate 起始日期，定义报表统计的开始日期
     * @param endDate 结束日期，定义报表统计的结束日期
     * @return 返回包含统计数据的列表，具体内容基于订单数据
     */
    @Override
    public List<?> generateReport(LocalDate startDate, LocalDate endDate, Long restaurantId, String granularity) {
        return orderMapper.getReportData(startDate, endDate, restaurantId, granularity);
    }

    @Override
    public KpiCompareDTO getKpiCompare(
            LocalDate startDate,
            LocalDate endDate,
            Long restaurantId,
            String granularity
    ) {
        // 只对「月」做同比
        if (!"month".equals(granularity)) {
            KpiCompareDTO dto = new KpiCompareDTO();
            dto.setCurrent(orderMapper.getKpi(startDate, endDate, restaurantId));
            dto.setPrevious(null);
            return dto;
        }

        // 当前自然月
        LocalDate[] currentPeriod = getNaturalMonth(startDate);

        KpiData current = orderMapper.getKpi(
                currentPeriod[0],
                currentPeriod[1],
                restaurantId
        );

        // 上年同月（同比）
        LocalDate lastYearSameMonth = startDate.minusYears(1);
        LocalDate[] prevPeriod = getNaturalMonth(lastYearSameMonth);

        KpiData previous = orderMapper.getKpi(
                prevPeriod[0],
                prevPeriod[1],
                restaurantId
        );

        KpiCompareDTO dto = new KpiCompareDTO();
        dto.setCurrent(current);
        dto.setPrevious(previous);
        return dto;
    }

    private LocalDate[] getNaturalMonth(LocalDate anyDay) {
        LocalDate start = anyDay.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = anyDay.with(TemporalAdjusters.lastDayOfMonth());
        return new LocalDate[]{start, end};
    }

    @Override
    public List<DishSalesDTO> getDishSales(
            LocalDate startDate,
            LocalDate endDate,
            Long restaurantId
    ) {
        return orderMapper.getDishSalesStat(startDate, endDate, restaurantId);
    }

}
