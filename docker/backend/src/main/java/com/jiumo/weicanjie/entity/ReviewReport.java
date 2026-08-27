package com.jiumo.weicanjie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户评价举报实体类，对应 review_report 表。
 * 用于处理用户对评价内容的举报。
 */
@Data
@TableName("review_report")
public class ReviewReport {

    @TableId(type = IdType.AUTO)
    private Long id;  // 举报记录ID

    private Long reviewId;  // 被举报的评价ID
    private Long reporterId;  // 举报用户ID
    private Long restaurantId;  // 餐厅ID（便于后台筛选）

    private String reason;  // 举报原因
    private String detail;  // 详细描述
    private String images;  // 举报图片（JSON字符串）

    private Integer status;  // 状态：0待审核 1通过 2驳回
    private String resultComment;  // 审核说明
    private Integer reviewAction;  // 审核后对评价的处理：0无处理/1删除等

    private LocalDateTime createdTime;  // 举报时间
    private LocalDateTime updatedTime;  // 更新时间
}

