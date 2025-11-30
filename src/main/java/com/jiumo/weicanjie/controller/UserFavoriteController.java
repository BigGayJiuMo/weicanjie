package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.UserFavorite;
import com.jiumo.weicanjie.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 添加餐厅收藏
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
     */
    @PostMapping("/remove")
    public Result<String> removeFavorite(@RequestBody UserFavorite req) {
        if (req.getUserId() == null || req.getRestaurantId() == null) {
            return Result.error("缺少 userId 或 restaurantId");
        }
        return userFavoriteService.removeRestaurantFavorite(req.getUserId(), req.getRestaurantId());
    }

    /**
     * 查询是否已收藏
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
     */
    @GetMapping("/list")
    public Result<Object> listFavorites(@RequestParam Long userId) {
        return Result.success(userFavoriteService.getFavoriteRestaurantList(userId));
    }
}
