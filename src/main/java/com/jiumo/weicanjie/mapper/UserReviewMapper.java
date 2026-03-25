package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserReview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 用户评价数据访问接口（Mapper），提供对 user_review 表的操作。
 * 主要功能包括查询餐厅的评价、获取用户的评价历史、后台管理查询等。
 */
@Mapper
public interface UserReviewMapper extends BaseMapper<UserReview> {

    /**
     * 商家查看评价，带用户昵称、头像和回复内容。
     * 返回指定餐厅所有已通过审核的评价信息，并包含用户的昵称、头像及商家的回复内容。
     *
     * @param restaurantId 餐厅ID
     * @return 返回该餐厅所有已审核的用户评价信息
     */
    @Select("SELECT r.*," +
            "r.reply_content AS replyContent," +
            "r.reply_time AS replyTime, " +
            "u.nickname AS username, " +
            "u.avatar_url AS avatar " +
            "FROM user_review r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.restaurant_id = #{restaurantId} " +
            "AND r.status = 1 " +
            "AND r.review_status = 1 " +
            "ORDER BY r.created_time DESC")
    List<Map<String, Object>> selectReviewsByRestaurantId(Long restaurantId);

    /**
     * 根据用户ID获取该用户的所有评价。
     * 返回指定用户的所有评价记录，包括餐厅的基本信息，如名称和logo。
     *
     * @param userId 用户ID
     * @return 返回该用户所有评价的详细信息
     */
    @Select("SELECT r.*, " +
            "r.reply_content AS replyContent, " +
            "r.reply_time AS replyTime, " +
            "res.name AS restaurant_name, " +
            "res.logo_url AS restaurant_logo " +
            "FROM user_review r " +
            "LEFT JOIN restaurant res ON r.restaurant_id = res.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_time DESC")
    List<Map<String, Object>> selectByUserId(Long userId);

    /**
     * 后台管理查询评价列表，支持根据餐厅ID和评价状态筛选。
     * 返回所有符合条件的评价记录，用于后台管理系统的审核。
     *
     * @param restaurantId 餐厅ID（可选）
     * @param reviewStatus 评价状态：-1表示不筛选，其他值筛选特定状态
     * @return 返回符合条件的评价记录列表
     */
    @Select({
            "<script>",
            "SELECT",
            " r.*,",
            " u.nickname AS username,",
            " u.avatar_url AS avatar",
            "FROM user_review r",
            "LEFT JOIN users u ON r.user_id = u.id",
            "WHERE 1=1",
            " <if test='restaurantId != null'>",
            "   AND r.restaurant_id = #{restaurantId}",
            " </if>",
            " <if test='reviewStatus != -1'>",
            "   AND r.review_status = #{reviewStatus}",
            " </if>",
            "ORDER BY r.created_time DESC",
            "</script>"
    })
    List<Map<String, Object>> adminSelectReviewsFlexible(
            @Param("restaurantId") Long restaurantId,
            @Param("reviewStatus") Integer reviewStatus
    );

    /**
     * 商家回复用户评价。
     * 更新指定评价的回复内容和回复时间。
     *
     * @param reviewId   评价ID
     * @param replyContent 商家回复内容
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE user_review " +
            "SET reply_content = #{replyContent}, reply_time = NOW() " +
            "WHERE id = #{reviewId}")
    int replyReview(@Param("reviewId") Long reviewId,
                    @Param("replyContent") String replyContent);

    /**
     * 删除评价的回复内容。
     * 清除指定评价的回复内容和回复时间。
     *
     * @param reviewId 评价ID
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE user_review " +
            "SET reply_content = NULL, reply_time = NULL " +
            "WHERE id = #{reviewId}")
    int deleteReply(@Param("reviewId") Long reviewId);

    @Select("SELECT rating FROM user_review " +
            "WHERE restaurant_id = #{restaurantId} " +
            "AND status = 1 " +
            "AND review_status = 1")      //  只算审核通过的
    List<Map<String, Object>> selectValidReviewsForRating(Long restaurantId);

    @Delete("DELETE FROM review_report WHERE review_id = #{reviewId}")
    int deleteReportsByReviewId(Long reviewId);
}
