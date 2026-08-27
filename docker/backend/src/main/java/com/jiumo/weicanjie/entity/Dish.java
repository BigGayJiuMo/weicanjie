// Dish.java
package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类，对应 dish 表。
 * 记录餐厅下的菜品基础信息。
 */
@Data
@TableName("dish")
public class Dish {

    @TableId(type = IdType.AUTO)
    private Long id;  // 菜品ID

    private String name;  // 菜品名称
    private BigDecimal price;  // 销售价格
    private String description;  // 菜品描述
    private String imageUrl;  // 图片URL

    private Long categoryId;  // 分类ID
    private Long restaurantId;  // 餐厅ID

    private Integer status;  // 状态：1=上架，0=下架
    private Integer stock;  // 库存数量

    private String ingredients;  // 食材信息
    private String taste;  // 口味描述
    private String weight;  // 分量描述
    private Integer monthlySales;  // 月销量

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;  // 更新时间
}