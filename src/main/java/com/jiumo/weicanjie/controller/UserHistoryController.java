package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.service.UserHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/history")
public class UserHistoryController {

    @Autowired
    private UserHistoryService historyService;

    @PostMapping("/record")
    public Result<String> record(@RequestParam Long userId,
                                 @RequestParam Long restaurantId) {

        historyService.recordView(userId, restaurantId);
        return Result.success("已记录浏览");
    }

    @GetMapping("/list")
    public Result<Object> list(@RequestParam Long userId,
                               @RequestParam(defaultValue = "20") int limit) {

        return Result.success(historyService.getRecentHistory(userId, limit));
    }
}
