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

        // 先查是否存在
        UserHistory history = this.getOne(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .eq(UserHistory::getRestaurantId, restaurantId)
        );

        if (history == null) {
            // 新建记录
            history = new UserHistory();
            history.setUserId(userId);
            history.setRestaurantId(restaurantId);
            history.setViewedTime(LocalDateTime.now());

            this.save(history);
        } else {
            // 已存在 → 刷新时间
            history.setViewedTime(LocalDateTime.now());

            this.updateById(history);
        }
    }

    @Override
    public List<UserHistory> getRecentHistory(Long userId, int limit) {
        return this.list(
                new LambdaQueryWrapper<UserHistory>()
                        .eq(UserHistory::getUserId, userId)
                        .orderByDesc(UserHistory::getViewedTime)
                        .last("LIMIT " + limit)
        );
    }
}
