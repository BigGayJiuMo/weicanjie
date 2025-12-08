package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review_report")
public class ReviewReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reviewId;
    private Long reporterId;
    private Long restaurantId;

    private String reason;
    private String detail;

    private String images;  // 存 JSON 字符串

    private Integer status;  // 0待审核 1通过 2驳回

    private String resultComment;
    private Integer reviewAction;

    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
