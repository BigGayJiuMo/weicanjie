package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserFavorite;
import com.jiumo.weicanjie.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户收藏管理控制器
 * 该控制器提供用户餐厅收藏的相关操作，如添加、取消收藏、查询收藏状态及获取用户收藏的餐厅列表。
 */
@RestController
@RequestMapping("/favorite")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加餐厅收藏
     *
     * 该接口用于将指定餐厅添加到用户的收藏列表。
     *
     * @param req 包含用户ID和餐厅ID的收藏请求对象
     * @return 返回操作结果，成功时返回“添加成功”，失败时返回错误信息
     */
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestBody UserFavorite req) {
        if (req.getUserId() == null || req.getRestaurantId() == null) {
            return Result.error("缺少 userId 或 restaurantId");
        }
        return userFavoriteService.addRestaurantFavorite(req.getUserId(), req.getRestaurantId());
    }

    /**
     * 取消餐厅收藏
     *
     * 该接口用于将指定餐厅从用户的收藏列表中移除。
     *
     * @param req 包含用户ID和餐厅ID的请求对象
     * @return 返回操作结果，成功时返回“移除成功”，失败时返回错误信息
     */
    @PostMapping("/remove")
    public Result<String> removeFavorite(@RequestBody UserFavorite req) {
        if (req.getUserId() == null || req.getRestaurantId() == null) {
            return Result.error("缺少 userId 或 restaurantId");
        }
        return userFavoriteService.removeRestaurantFavorite(req.getUserId(), req.getRestaurantId());
    }

    /**
     * 查询是否已收藏指定餐厅
     *
     * 该接口用于检查指定用户是否已收藏某个餐厅。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @return 返回是否已收藏该餐厅的布尔值
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long restaurantId
    ) {
        boolean exists = userFavoriteService.isRestaurantFavorite(userId, restaurantId);
        return Result.success(exists);
    }

    /**
     * 获取用户收藏的餐厅列表
     *
     * 该接口用于查询用户收藏的所有餐厅。
     *
     * @param userId 用户ID
     * @return 返回用户收藏的餐厅列表
     */
    @GetMapping("/list")
    public Result<Object> listFavorites(@RequestParam Long userId) {
        return Result.success(userFavoriteService.getFavoriteRestaurantList(userId));
    }
}
