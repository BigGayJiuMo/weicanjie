package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.UserHistory;
import java.util.List;

public interface UserHistoryService extends IService<UserHistory> {

    void recordView(Long userId, Long restaurantId);

    List<UserHistory> getRecentHistory(Long userId, int limit);
}
