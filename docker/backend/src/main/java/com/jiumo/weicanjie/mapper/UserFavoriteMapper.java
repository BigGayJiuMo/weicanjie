package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏数据访问接口（Mapper），提供对 user_favorite 表的操作。
 * 该接口继承了 MyBatis Plus 提供的 BaseMapper，具有基本的 CRUD 操作功能。
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    // 该接口继承了 BaseMapper，因此自动具备了增、删、改、查的基本功能。
}
