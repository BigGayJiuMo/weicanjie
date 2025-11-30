package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("restaurant_category")
public class RestaurantCategory {

    @TableId(type = IdType.INPUT)
    private Integer id;

    private String name;
}
