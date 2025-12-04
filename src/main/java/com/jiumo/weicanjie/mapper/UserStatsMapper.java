package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserStats;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserStatsMapper extends BaseMapper<UserStats> {

    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    UserStats findByUserId(Long userId);

    @Insert("INSERT INTO user_stats (user_id, favorite_count, order_count, review_count, total_spent, created_time, updated_time) " +
            "VALUES (#{userId}, 0, 0, 0, 0.00, NOW(), NOW())")
    int insert(UserStats stats);

    @Update("UPDATE user_stats SET favorite_count = #{favoriteCount}, order_count = #{orderCount}, review_count = #{reviewCount}, total_spent = #{totalSpent}, updated_time = NOW() WHERE user_id = #{userId}")
    int update(UserStats stats);

    //  评价数 +1
    @Update("UPDATE user_stats SET review_count = review_count + 1 WHERE user_id = #{userId}")
    int incrementReviewCount(Long userId);

    //  评价数 -1
    @Update("UPDATE user_stats SET review_count = CASE WHEN review_count > 0 THEN review_count - 1 ELSE 0 END WHERE user_id = #{userId}")
    int decrementReviewCount(Long userId);
}
