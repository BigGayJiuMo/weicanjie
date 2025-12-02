package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("restaurant_images")
public class RestaurantImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long restaurantId;

    private String imageUrl;

    private Integer sortOrder;
}
