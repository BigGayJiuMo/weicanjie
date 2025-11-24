package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weicanjie")
public class WeicanjieController {

    @GetMapping("/info") // 必须是 "/info"
    public Result getWeicanjieInfo() {
        // 业务逻辑（示例）
        return Result.success("餐厅信息");
    }
}