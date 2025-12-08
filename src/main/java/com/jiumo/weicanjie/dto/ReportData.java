package com.jiumo.weicanjie.dto;

import java.math.BigDecimal;

/**
 * 报表数据传输对象
 * 该类封装了报表的数据字段，包括餐厅名称、订单数量、总销售额和菜品销量等信息。
 */
public class ReportData {

    private String restaurantName; // 餐厅名称
    private int orderCount;       // 订单数量
    private BigDecimal totalSales; // 总销售额
    private int totalItemsSold;   // 总菜品销量
}
