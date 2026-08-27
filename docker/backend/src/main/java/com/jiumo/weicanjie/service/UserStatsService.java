package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.UserStats;

/**
 * 用户统计服务接口，定义了用户统计相关的业务逻辑。
 * 该接口继承自 MyBatis Plus 提供的 IService 接口，包含了对用户统计信息的常见操作。
 */
public interface UserStatsService extends IService<UserStats> {

    /**
     * 根据用户ID获取用户的统计信息。
     *
     * @param userId 用户ID
     * @return 返回该用户的统计信息（如果存在）
     */
    UserStats getStats(Long userId);

    /**
     * 为指定用户创建默认的统计信息记录。
     *
     * @param userId 用户ID
     */
    void createDefaultStats(Long userId);

    /**
     * 增加用户的评价数量。
     *
     * @param userId 用户ID
     */
    void incrementReviewCount(Long userId);

    /**
     * 减少用户的评价数量。
     *
     * @param userId 用户ID
     */
    void decrementReviewCount(Long userId);
}
