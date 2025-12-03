package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.UserHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/history")
public class UserHistoryController {

    @Autowired
    private UserHistoryService historyService;

    @PostMapping("/record")
    public Result<String> record(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long restaurantId,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        // 如果是 JSON 发送，则从 body 取
        if (body != null) {
            if (userId == null && body.get("userId") != null) {
                userId = Long.valueOf(body.get("userId").toString());
            }
            if (restaurantId == null && body.get("restaurantId") != null) {
                restaurantId = Long.valueOf(body.get("restaurantId").toString());
            }
        }

        if (userId == null || restaurantId == null) {
            return Result.error("缺少 userId 或 restaurantId");
        }

        historyService.recordView(userId, restaurantId);
        return Result.success("已记录浏览");
    }


    @GetMapping("/list")
    public Result<Object> list(@RequestParam Long userId,
                               @RequestParam(defaultValue = "20") int limit) {

        return Result.success(historyService.getRecentHistory(userId, limit));
    }
}
