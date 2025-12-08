package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserStats;
import org.apache.ibatis.annotations.*;

/**
 * 用户统计数据访问接口（Mapper），提供对 user_stats 表的操作。
 * 包括根据用户ID查询统计信息、插入用户统计记录、更新用户统计信息、增加/减少评价数量等功能。
 */
@Mapper
public interface UserStatsMapper extends BaseMapper<UserStats> {

    /**
     * 根据用户ID查询用户的统计信息。
     *
     * @param userId 用户ID
     * @return 返回该用户的统计信息（如果存在）
     */
    @Select("SELECT * FROM user_stats WHERE user_id = #{userId}")
    UserStats findByUserId(Long userId);

    /**
     * 插入新的用户统计记录。
     * 该方法会为用户创建一个初始的统计记录，默认收藏数、订单数、评价数、总消费金额均为零。
     *
     * @param stats 包含用户统计信息的 UserStats 对象
     * @return 返回插入的记录数，0表示未插入任何记录
     */
    @Insert("INSERT INTO user_stats (user_id, favorite_count, order_count, review_count, total_spent, created_time, updated_time) " +
            "VALUES (#{userId}, 0, 0, 0, 0.00, NOW(), NOW())")
    int insert(UserStats stats);

    /**
     * 更新指定用户的统计信息。
     *
     * @param stats 包含更新信息的 UserStats 对象
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE user_stats SET favorite_count = #{favoriteCount}, order_count = #{orderCount}, review_count = #{reviewCount}, total_spent = #{totalSpent}, updated_time = NOW() WHERE user_id = #{userId}")
    int update(UserStats stats);

    /**
     * 增加用户的评价数。
     * 该方法将增加指定用户的 `review_count`（评价数量）字段的值。
     *
     * @param userId 用户ID
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE user_stats SET review_count = review_count + 1 WHERE user_id = #{userId}")
    int incrementReviewCount(Long userId);

    /**
     * 减少用户的评价数。
     * 该方法将减少指定用户的 `review_count`（评价数量）字段的值，若评价数为零则不再减少。
     *
     * @param userId 用户ID
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE user_stats SET review_count = CASE WHEN review_count > 0 THEN review_count - 1 ELSE 0 END WHERE user_id = #{userId}")
    int decrementReviewCount(Long userId);
}
