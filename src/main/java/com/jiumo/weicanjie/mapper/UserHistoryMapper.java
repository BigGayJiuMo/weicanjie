package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {

    List<UserHistory> selectRecentHistory(
            @Param("userId") Long userId,
            @Param("limit") int limit
    );
}
