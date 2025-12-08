package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.ReviewReport;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 评价举报记录数据访问接口（Mapper），提供对 review_report 表的操作。
 * 主要功能是查询评价举报记录，并联接相关的用户评价信息和用户信息。
 */
@Mapper
public interface ReviewReportMapper extends BaseMapper<ReviewReport> {

    /**
     * 获取管理员查看的评价举报列表。
     * 根据举报状态和餐厅ID过滤，并返回每条举报的详细信息（包括评价内容、评分、用户信息等）。
     *
     * @param status        举报状态：用于过滤结果，null或-1表示不过滤，其他值过滤特定状态
     * @param restaurantId  餐厅ID：用于根据餐厅筛选举报记录，null表示不过滤
     * @return 返回符合条件的举报记录列表，包含每条举报的详细信息（包括评价内容、评分、用户信息等）
     */
    @Select({
            "<script>",
            "SELECT r.*, ",
            "ur.content AS reviewContent, ",
            "ur.rating, ",
            "ur.image_urls, ",
            "u.nickname AS username, ",
            "u.avatar_url AS avatar ",
            "FROM review_report r ",
            "LEFT JOIN user_review ur ON r.review_id = ur.id ",
            "LEFT JOIN users u ON ur.user_id = u.id ",
            "WHERE 1=1 ",

            "<if test='status != null and status != -1'>",
            "   AND r.status = #{status} ",
            "</if>",

            "<if test='restaurantId != null'>",
            "   AND r.restaurant_id = #{restaurantId} ",
            "</if>",

            "ORDER BY r.created_time DESC",
            "</script>"
    })
    List<Map<String, Object>> adminList(
            @Param("status") Integer status,
            @Param("restaurantId") Long restaurantId
    );
}
