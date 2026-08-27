package com.jiumo.weicanjie.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReportResponseDTO {
    private List<?> data;
    private KpiCompareDTO kpi;
    private List<DishSalesDTO> dishSales;
}