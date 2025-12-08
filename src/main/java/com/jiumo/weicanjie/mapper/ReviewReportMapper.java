package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.ReviewReport;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewReportMapper extends BaseMapper<ReviewReport> {

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
