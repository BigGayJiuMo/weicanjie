package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_stats")
public class UserStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer favoriteCount;
    private Integer orderCount;
    private Integer reviewCount;

    private BigDecimal totalSpent;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
