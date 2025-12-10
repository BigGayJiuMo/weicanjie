package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 餐厅实体类，对应 restaurant 表。
 * 用于表示餐厅的基本信息，包括餐厅名称、地址、评分等。
 */
@Data
@TableName("restaurant")
public class Restaurant {

    @TableId(type = IdType.AUTO)
    private Long id;  // 餐厅ID

    private String name;  // 餐厅名称
    private String description;  // 餐厅描述
    private String contactPhone;  // 联系电话
    private String logoUrl;  // 餐厅logo URL
    private String address;  // 餐厅地址
    private Integer status;  // 餐厅状态（例如：营业中、已停业）

    private Double avgRating;  // 餐厅平均评分
    private Integer monthlySales;  // 月销售量
    private Double packingFee;  // 打包费用
    private Integer businessStatus;  // 餐厅营业状态
    private Integer manualBusinessStatus; //商家手动设置的营业状态
    @TableField("category_type")
    private Integer categoryType;  // 餐厅分类类型

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;  // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;  // 更新时间

    // 非数据库字段
    @TableField(exist = false)
    private List<RestaurantBusinessHours> restaurantBusinessHours;  // 餐厅营业时间

    @TableField(exist = false)
    private List<DishCategory> categories;  // 菜品分类

    @TableField(exist = false)
    private List<String> shopImages;  // 商家展示图片

    // 新增字段用于保存营业状态文本和样式
    @TableField(exist = false)
    private String businessStatusText;  // 显示营业状态文本

    @TableField(exist = false)
    private String businessStatusClass;  // 显示营业状态的CSS类
}
