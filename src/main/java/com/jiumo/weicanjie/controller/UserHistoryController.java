package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.UserHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户浏览历史管理控制器
 * 该控制器提供记录用户浏览历史和查询用户历史记录的功能。
 */
@RestController
@RequestMapping("/history")
public class UserHistoryController {

    @Autowired
    private UserHistoryService historyService;

    /**
     * 记录用户浏览历史
     *
     * 该接口用于记录用户浏览餐厅的历史数据。当用户访问餐厅时，通过该接口将浏览信息保存到历史记录中。
     *
     * @param userId 用户ID
     * @param restaurantId 餐厅ID
     * @param body 请求体，包含可能缺失的用户ID和餐厅ID
     * @return 返回记录结果，成功时返回“已记录浏览”，失败时返回错误信息
     */
    @PostMapping("/record")
    public Result<String> record(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long restaurantId,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        // 如果请求体（body）中包含数据，则从body中取出userId和restaurantId
        if (body != null) {
            if (userId == null && body.get("userId") != null) {
                userId = Long.valueOf(body.get("userId").toString());
            }
            if (restaurantId == null && body.get("restaurantId") != null) {
                restaurantId = Long.valueOf(body.get("restaurantId").toString());
            }
        }

        // 校验userId和restaurantId是否有效
        if (userId == null || restaurantId == null) {
            return Result.error("缺少 userId 或 restaurantId");
        }

        // 记录用户浏览历史
        historyService.recordView(userId, restaurantId);
        return Result.success("已记录浏览");
    }

    /**
     * 获取用户最近的浏览历史记录
     *
     * 该接口用于获取指定用户的最近浏览历史记录，可以限制返回记录的数量。
     *
     * @param userId 用户ID
     * @param limit 返回记录的数量，默认为20条
     * @return 返回指定数量的用户浏览历史记录
     */
    @GetMapping("/list")
    public Result<Object> list(@RequestParam Long userId,
                               @RequestParam(defaultValue = "20") int limit) {

        return Result.success(historyService.getRecentHistory(userId, limit));
    }
}
