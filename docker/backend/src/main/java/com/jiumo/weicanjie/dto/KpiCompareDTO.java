package com.jiumo.weicanjie.dto;

import lombok.Data;

@Data
public class KpiCompareDTO {
    private KpiData current;
    private KpiData previous;
}