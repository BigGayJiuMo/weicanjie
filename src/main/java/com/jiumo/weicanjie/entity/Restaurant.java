package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("restaurant")
public class Restaurant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String contactPhone;
    private String logoUrl;
    private String address;
    private Integer status;

    private Double avgRating;
    private Integer monthlySales;
    private Double packingFee;
    private Integer businessStatus;

    @TableField("category_type")
    private Integer categoryType;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    // 非数据库字段 - 营业时间
    @TableField(exist = false)
    private List<BusinessHours> businessHours;

    // 非数据库字段 - 菜品分类及菜品
    @TableField(exist = false)
    private List<DishCategory> categories;

    // 非数据库字段 - 商家展示图片
    @TableField(exist = false)
    private List<String> shopImages;
}
