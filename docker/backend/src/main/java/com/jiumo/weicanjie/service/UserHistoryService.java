package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.UserHistory;

import java.util.List;

/**
 * 用户历史记录服务接口，提供记录和查询用户历史的功能。
 * 主要操作包括记录用户查看餐厅的历史，获取用户的最近历史记录等。
 */
public interface UserHistoryService extends IService<UserHistory> {

    /**
     * 记录用户查看餐厅的历史。
     * 如果历史记录已存在，则更新时间；如果历史记录不存在，则创建新的记录。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     */
    void recordView(Long userId, Long restaurantId);

    /**
     * 获取用户的最近查看历史记录。
     * 返回限定数量的最近记录。
     *
     * @param userId 用户ID
     * @param limit 限制返回的历史记录数量
     * @return 返回用户最近查看的历史记录列表
     */
    List<UserHistory> getRecentHistory(Long userId, int limit);
}
