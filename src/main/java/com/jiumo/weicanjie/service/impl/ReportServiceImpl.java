package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<?> generateReport(LocalDate startDate, LocalDate endDate) {
        return orderMapper.getReportData(startDate, endDate);
    }
}
