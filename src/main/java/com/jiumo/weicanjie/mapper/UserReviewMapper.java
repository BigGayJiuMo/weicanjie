package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserReview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserReviewMapper extends BaseMapper<UserReview> {

    /** 商家查看评价（带用户昵称、头像、回复内容） */
    @Select("SELECT r.*," +
            "r.reply_content AS replyContent,"+
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


    /** 根据用户获取他的所有评价 */
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


    /** 后台审核列表（super 管理员使用） */
    @Select({
            "<script>",
            "SELECT r.*, ",
            "r.reply_content AS replyContent, ",
            "r.reply_time AS replyTime, ",
            "u.nickname AS username, ",
            "u.avatar_url AS avatar ",
            "FROM user_review r ",
            "LEFT JOIN users u ON r.user_id = u.id ",
            "WHERE r.restaurant_id = #{restaurantId} ",
            "<if test='reviewStatus != -1'>",
            "AND r.review_status = #{reviewStatus} ",
            "</if>",
            "ORDER BY r.created_time DESC",
            "</script>"
    })
    List<Map<String, Object>> adminSelectReviews(
            @Param("restaurantId") Long restaurantId,
            @Param("reviewStatus") Integer reviewStatus);


    /** 回复评价 */
    @Update("UPDATE user_review " +
            "SET reply_content = #{replyContent}, reply_time = NOW() " +
            "WHERE id = #{reviewId}")
    int replyReview(@Param("reviewId") Long reviewId,
                    @Param("replyContent") String replyContent);


    /** 删除回复 */
    @Update("UPDATE user_review " +
            "SET reply_content = NULL, reply_time = NULL " +
            "WHERE id = #{reviewId}")
    int deleteReply(@Param("reviewId") Long reviewId);
}
