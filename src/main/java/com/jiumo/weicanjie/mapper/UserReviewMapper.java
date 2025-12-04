package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserReviewMapper extends BaseMapper<UserReview> {

    @Select("SELECT r.*, " +
            "u.nickname AS username, " +
            "u.avatar_url AS avatar, " +
            "res.name AS restaurant_name, " +
            "res.logo_url AS restaurant_logo " +
            "FROM user_review r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "LEFT JOIN restaurant res ON r.restaurant_id = res.id " +
            "WHERE r.restaurant_id = #{restaurantId} " +
            "AND r.status = 1 " +
            "ORDER BY r.created_time DESC")
    List<Map<String, Object>> selectReviewsByRestaurantId(Long restaurantId);


    @Select("SELECT r.*, " +
            "res.name AS restaurant_name, " +
            "res.logo_url AS restaurant_logo " +
            "FROM user_review r " +
            "LEFT JOIN restaurant res ON r.restaurant_id = res.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_time DESC")
    List<Map<String, Object>> selectByUserId(Long userId);
}
