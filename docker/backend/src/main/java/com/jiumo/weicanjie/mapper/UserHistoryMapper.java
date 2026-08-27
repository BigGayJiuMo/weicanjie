package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户浏览历史数据访问接口（Mapper），提供对 user_history 表的操作。
 * 包括查询用户最近浏览的餐厅历史记录，并返回餐厅的相关信息。
 */
@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {

    /**
     * 查询用户的最近浏览历史，返回餐厅的相关信息。
     *
     * @param userId 用户ID
     * @param limit  限制查询结果的条数
     * @return 返回用户的浏览历史记录列表，每条记录包含餐厅的基本信息（如名称、图片、评分等）
     */
    List<UserHistory> selectRecentHistory(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
}
