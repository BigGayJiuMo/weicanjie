package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_history")
public class UserHistory {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private LocalDateTime viewedTime;
}
