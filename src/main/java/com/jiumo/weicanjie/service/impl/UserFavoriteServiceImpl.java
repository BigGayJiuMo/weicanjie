package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserFavorite;
import com.jiumo.weicanjie.entity.UserStats;
import com.jiumo.weicanjie.mapper.UserFavoriteMapper;
import com.jiumo.weicanjie.service.UserFavoriteService;
import com.jiumo.weicanjie.service.UserStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * 用户餐厅收藏服务实现类，提供对用户收藏餐厅的相关操作。
 * 包括添加收藏、取消收藏、判断收藏状态以及获取用户收藏的餐厅列表。
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite>
        implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private UserStatsService userStatsService;

    /**
     * 添加餐厅到用户的收藏列表。
     * 如果餐厅已经被收藏，则返回错误提示。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果，包括成功或失败的提示信息
     */
    @Override
    @Transactional
    public Result<String> addRestaurantFavorite(Long userId, Long restaurantId) {

        // 判断餐厅是否已被收藏
        UserFavorite exists = userFavoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );

        // 如果已经收藏，返回提示信息
        if (exists != null) {
            return Result.error("该餐厅已收藏");
        }

        // 创建新的收藏记录
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setRestaurantId(restaurantId);
        userFavorite.setCreatedTime(LocalDateTime.now());

        // 插入收藏记录到数据库
        userFavoriteMapper.insert(userFavorite);

        // 更新用户的收藏统计信息
        updateFavoriteStats(userId);

        return Result.success("收藏成功");
    }

    /**
     * 从用户的收藏列表中移除餐厅。
     * 如果餐厅没有被收藏，则不做任何操作。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回操作结果，包括成功或失败的提示信息
     */
    @Override
    @Transactional
    public Result<String> removeRestaurantFavorite(Long userId, Long restaurantId) {

        // 从收藏中删除指定餐厅
        userFavoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );

        // 更新用户的收藏统计信息
        updateFavoriteStats(userId);

        return Result.success("取消收藏成功");
    }

    /**
     * 统计用户收藏的餐厅数量。
     *
     * @param userId 用户ID
     * @return 返回用户收藏的餐厅数量
     */
    @Override
    public Integer getRestaurantFavoriteCount(Long userId) {
        // 查询用户收藏的餐厅数量
        Long count = userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .isNotNull(UserFavorite::getRestaurantId)  // 确保餐厅ID不为null
        );
        return count.intValue();
    }

    /**
     * 更新用户的收藏统计信息（favoriteCount）。
     * 在添加或删除收藏时调用该方法，以确保用户统计数据的最新状态。
     *
     * @param userId 用户ID
     */
    private void updateFavoriteStats(Long userId) {
        // 获取用户当前的收藏数量
        Integer count = getRestaurantFavoriteCount(userId);

        // 获取并更新用户的收藏统计
        UserStats stats = userStatsService.getOne(
                new LambdaQueryWrapper<UserStats>()
                        .eq(UserStats::getUserId, userId)
        );

        if (stats != null) {
            stats.setFavoriteCount(count);  // 更新收藏数量
            stats.setUpdatedTime(LocalDateTime.now());  // 更新更新时间
            userStatsService.updateById(stats);
        }
    }

    /**
     * 判断用户是否已经收藏了指定的餐厅。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 如果餐厅已收藏，返回true；否则返回false
     */
    @Override
    public boolean isRestaurantFavorite(Long userId, Long restaurantId) {
        // 查询是否已收藏
        UserFavorite exists = userFavoriteMapper.selectOne(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getRestaurantId, restaurantId)
        );
        return exists != null;  // 如果找到了该记录，说明已收藏
    }

    /**
     * 获取用户收藏的餐厅列表。
     *
     * @param userId 用户ID
     * @return 返回用户收藏的餐厅列表，包含餐厅的基本信息
     */
    @Override
    public List<Map<String, Object>> getFavoriteRestaurantList(Long userId) {
        // 查询用户收藏的餐厅信息
        return userFavoriteMapper.selectMaps(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .select(UserFavorite::getRestaurantId, UserFavorite::getCreatedTime)  // 获取餐厅ID和收藏时间
        );
    }
}
