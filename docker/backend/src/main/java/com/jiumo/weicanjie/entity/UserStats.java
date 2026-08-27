package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户统计信息实体类，对应 user_stats 表。
 * 用于记录用户的行为统计，例如收藏数、订单数等。
 */
@Data
@TableName("user_stats")
public class UserStats {

    @TableId(type = IdType.AUTO)
    private Long id;  // 主键ID

    private Long userId;  // 用户ID

    private Integer favoriteCount;  // 收藏数量
    private Integer orderCount;  // 下单次数
    private Integer reviewCount;  // 评价次数

    private BigDecimal totalSpent;  // 总消费金额

    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
}
