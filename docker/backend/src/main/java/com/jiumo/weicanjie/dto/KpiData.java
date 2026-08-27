package com.jiumo.weicanjie.dto;

import lombok.Data;

import java.math.BigDecimal;

// dto/KpiData.java
@Data
public class KpiData {
    private Integer orderCount;
    private BigDecimal totalSales;
    private Integer totalItemsSold;
}
