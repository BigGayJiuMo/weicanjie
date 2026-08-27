package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.mapper.UserStatsMapper;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户统计服务实现类，实现了 UserStatsService 接口中的业务逻辑。
 * 该类主要负责用户统计信息的获取、更新及创建等操作。
 */
@Service
public class UserStatsServiceImpl extends ServiceImpl<UserStatsMapper, UserStats> implements UserStatsService {

    @Autowired
    private UserStatsMapper statsMapper;  // 注入 UserStatsMapper，用于执行数据库操作

    /**
     * 根据用户ID获取用户的统计信息。
     *
     * @param userId 用户ID
     * @return 返回该用户的统计信息（如果存在）
     */
    @Override
    public UserStats getStats(Long userId) {
        return statsMapper.findByUserId(userId);  // 从数据库中获取指定用户的统计信息
    }

    /**
     * 为指定用户创建默认的统计信息记录。
     * 默认的统计信息包括收藏数、订单数、评价数、总消费金额等，初始值为零。
     *
     * @param userId 用户ID
     */
    @Override
    public void createDefaultStats(Long userId) {
        UserStats stats = new UserStats();  // 创建一个新的 UserStats 实例
        stats.setUserId(userId);  // 设置用户ID
        statsMapper.insert(stats);  // 插入新的用户统计记录
    }

    /**
     * 增加指定用户的评价数量。
     *
     * @param userId 用户ID
     */
    @Override
    public void incrementReviewCount(Long userId) {
        statsMapper.incrementReviewCount(userId);  // 调用 Mapper 增加评价数量
    }

    /**
     * 减少指定用户的评价数量。
     *
     * @param userId 用户ID
     */
    @Override
    public void decrementReviewCount(Long userId) {
        statsMapper.decrementReviewCount(userId);  // 调用 Mapper 减少评价数量
    }
}
