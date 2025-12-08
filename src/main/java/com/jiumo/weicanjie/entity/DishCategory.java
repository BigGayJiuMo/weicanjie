// Category.java
package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品分类实体类，对应 category 表。
 * 用于管理餐厅下的菜品分类信息。
 */
@Data
@TableName("category")
public class DishCategory {

    @TableId(type = IdType.AUTO)
    private Long id;  // 分类ID

    private String name;  // 分类名称
    private Long restaurantId;  // 所属餐厅ID
    private Integer sortOrder;  // 排序序号
    private Integer status;  // 状态：1=启用，0=停用

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    // 非数据库字段：分类下的菜品列表
    @TableField(exist = false)
    private List<Dish> dishes;
}