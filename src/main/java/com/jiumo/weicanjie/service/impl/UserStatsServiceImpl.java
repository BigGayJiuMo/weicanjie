package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.mapper.UserStatsMapper;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStatsServiceImpl extends ServiceImpl<UserStatsMapper, UserStats> implements UserStatsService {

    @Autowired
    private UserStatsMapper statsMapper;

    @Override
    public UserStats getStats(Long userId) {
        return statsMapper.findByUserId(userId);
    }

    @Override
    public void createDefaultStats(Long userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);
        statsMapper.insert(stats);
    }
}
