package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.UserHistory;
import com.jiumo.weicanjie.mapper.UserHistoryMapper;
import com.jiumo.weicanjie.service.UserHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户历史记录服务实现类，提供记录用户查看餐厅历史以及获取最近历史记录的功能。
 */
@Service
public class UserHistoryServiceImpl
        extends ServiceImpl<UserHistoryMapper, UserHistory>
        implements UserHistoryService {

    /**
     * 记录用户查看餐厅的历史
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     *
     * @note 若该餐厅历史记录已存在，则更新时间；若不存在，则新增记录
     * @note 保持最新的15条历史记录，超出部分删除
     */
    @Override
    public void recordView(Long userId, Long restaurantId) {

        // 1）查找是否存在相同的历史记录，避免重复
        UserHistory history = this.getOne(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .eq(UserHistory::getRestaurantId, restaurantId)
        );

        if (history == null) {
            // 不存在 → 新建历史记录
            history = new UserHistory();
            history.setUserId(userId);
            history.setRestaurantId(restaurantId);
            history.setViewedTime(LocalDateTime.now());
            this.save(history);  // 保存新记录
        } else {
            // 已存在 → 更新时间
            history.setViewedTime(LocalDateTime.now());
            this.updateById(history);  // 更新历史记录
        }

        // 清理多余的历史记录，最多保留最新的15条
        cleanOldHistory(userId, 15);
    }

    /**
     * 获取用户的最近历史记录
     *
     * @param userId 用户ID
     * @param limit 限制返回的历史记录数
     * @return 返回用户最近的历史记录列表
     */
    @Override
    public List<UserHistory> getRecentHistory(Long userId, int limit) {
        return baseMapper.selectRecentHistory(userId, limit);  // 从数据库中获取最近的历史记录
    }

    /**
     * 保留最新 limit 条历史记录，删除超出部分
     *
     * @param userId 用户ID
     * @param limit 保留的历史记录数量
     */
    private void cleanOldHistory(Long userId, int limit) {

        // 查询用户历史记录的总数量
        long count = this.count(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
        );

        // 如果总数量小于或等于限制，不需要清理
        if (count <= limit) return;

        int deleteCount = (int) (count - limit);  // 计算需要删除的历史记录数量

        // 查询最旧的 deleteCount 条记录
        List<UserHistory> oldList = this.list(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .orderByAsc(UserHistory::getViewedTime)  // 按时间升序排序，获取最旧的记录
                        .last("LIMIT " + deleteCount)  // 限制只查询需要删除的记录数量
        );

        // 如果找到需要删除的记录，执行删除操作
        if (oldList != null && !oldList.isEmpty()) {

            // 使用 Java 8 的流式操作获取所有记录的ID
            List<Long> ids = oldList.stream()
                    .map(UserHistory::getId)  // 提取历史记录的ID
                    .collect(java.util.stream.Collectors.toList());

            // 批量删除这些历史记录
            this.removeByIds(ids);
        }
    }

}
