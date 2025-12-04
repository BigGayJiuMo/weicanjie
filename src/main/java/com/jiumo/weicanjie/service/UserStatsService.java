package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.UserStats;

public interface UserStatsService extends IService<UserStats> {

    UserStats getStats(Long userId);

    void createDefaultStats(Long userId);

    void incrementReviewCount(Long userId);

    void decrementReviewCount(Long userId);
}
