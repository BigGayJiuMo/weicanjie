package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserFavorite;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.mapper.FavoriteMapper;
import com.jiumo.weicanjie.service.UserFavoriteService;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<FavoriteMapper, UserFavorite>
        implements UserFavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private UserStatsService userStatsService;

    /**
     * 添加收藏（餐厅）
     */
    @Override
    @Transactional
    public Result<String> addRestaurantFavorite(Long userId, Long restaurantId) {

        // 判断是否已收藏
        UserFavorite exists = favoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );

        if (exists != null) {
            return Result.error("该餐厅已收藏");
        }

        // 新增收藏
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setRestaurantId(restaurantId);
        userFavorite.setCreatedTime(LocalDateTime.now());

        favoriteMapper.insert(userFavorite);

        // 更新统计
        updateFavoriteStats(userId);

        return Result.success("收藏成功");
    }

    /**
     * 取消收藏（餐厅）
     */
    @Override
    @Transactional
    public Result<String> removeRestaurantFavorite(Long userId, Long restaurantId) {

        favoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );

        // 更新统计
        updateFavoriteStats(userId);

        return Result.success("取消收藏成功");
    }

    /**
     * 统计收藏餐厅数
     */
    @Override
    public Integer getRestaurantFavoriteCount(Long userId) {
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .isNotNull(UserFavorite::getRestaurantId)
        );
        return count.intValue();
    }

    /**
     * 更新收藏统计 UserStats.favoriteCount
     */
    private void updateFavoriteStats(Long userId) {
        Integer count = getRestaurantFavoriteCount(userId);

        UserStats stats = userStatsService.getOne(
                new LambdaQueryWrapper<UserStats>()
                        .eq(UserStats::getUserId, userId)
        );

        if (stats != null) {
            stats.setFavoriteCount(count);
            stats.setUpdatedTime(LocalDateTime.now());
            userStatsService.updateById(stats);
        }
    }
    /**
     * 判断用户是否收藏某餐厅
     */
    @Override
    public boolean isRestaurantFavorite(Long userId, Long restaurantId) {
        UserFavorite exists = favoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );
        return exists != null;
    }

    /**
     * 获取用户收藏的餐厅列表
     */
    @Override
    public List<Map<String, Object>> getFavoriteRestaurantList(Long userId) {
        return favoriteMapper.selectMaps(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .select(UserFavorite::getRestaurantId, UserFavorite::getCreatedTime)
        );
    }
}
