package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 餐厅分类实体类，对应 restaurant_category 表。
 * 如：中餐、烧烤、奶茶、炸鸡等。
 */
@Data
@TableName("restaurant_category")
public class RestaurantCategory {

    @TableId(type = IdType.INPUT)
    private Integer id;  // 分类ID（手动设置）

    private String name;  // 分类名称
}
