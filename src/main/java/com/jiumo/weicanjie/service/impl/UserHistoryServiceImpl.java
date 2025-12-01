package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.UserHistory;
import com.jiumo.weicanjie.mapper.UserHistoryMapper;
import com.jiumo.weicanjie.service.UserHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserHistoryServiceImpl
        extends ServiceImpl<UserHistoryMapper, UserHistory>
        implements UserHistoryService {

    @Override
    public void recordView(Long userId, Long restaurantId) {

        // 1）去重模式：查是否存在
        UserHistory history = this.getOne(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .eq(UserHistory::getRestaurantId, restaurantId)
        );

        if (history == null) {
            // 不存在 → 新建
            history = new UserHistory();
            history.setUserId(userId);
            history.setRestaurantId(restaurantId);
            history.setViewedTime(LocalDateTime.now());
            this.save(history);
        } else {
            // 已存在 → 更新时间
            history.setViewedTime(LocalDateTime.now());
            this.updateById(history);
        }
        cleanOldHistory(userId, 15);
    }


    @Override
    public List<UserHistory> getRecentHistory(Long userId, int limit) {
        return baseMapper.selectRecentHistory(userId, limit);
    }

    /**
     * 保留最新 limit 条历史记录，超出的全部删除
     */
    private void cleanOldHistory(Long userId, int limit) {

        // 查询总数量
        long count = this.count(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
        );

        if (count <= limit) return; // 不需要清理

        int deleteCount = (int) (count - limit);

        // 找出最旧的 deleteCount 条记录
        List<UserHistory> oldList = this.list(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .orderByAsc(UserHistory::getViewedTime)
                        .last("LIMIT " + deleteCount)
        );

        if (oldList != null && !oldList.isEmpty()) {

            // ⭐ Java 8 写法：Collectors.toList()
            List<Long> ids = oldList.stream()
                    .map(UserHistory::getId)
                    .collect(java.util.stream.Collectors.toList());

            // 删除
            this.removeByIds(ids);
        }
    }

}
