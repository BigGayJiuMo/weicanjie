package com.jiumo.weicanjie.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 报表数据传输对象
 * 该类封装了报表的数据字段，包括餐厅名称、订单数量、总销售额和菜品销量等信息。
 */
@Data
public class ReportData {

    private Long restaurantId;
    private String restaurantName;
    private String timeKey;
    private Integer orderCount;
    private BigDecimal totalSales;
    private Integer totalItemsSold;
}
