package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@TableName("user_favorite")
@Data
public class UserFavorite {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private LocalDateTime createdTime;
}
