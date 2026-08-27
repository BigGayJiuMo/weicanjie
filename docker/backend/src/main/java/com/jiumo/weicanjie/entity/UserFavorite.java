package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类，对应 user_favorite 表。
 * 用于记录用户收藏的餐厅。
 */
@Data
@TableName("user_favorite")
public class UserFavorite {

    private Long id;  // 收藏ID
    private Long userId;  // 用户ID
    private Long restaurantId;  // 餐厅ID

    private LocalDateTime createdTime;  // 收藏时间
}
