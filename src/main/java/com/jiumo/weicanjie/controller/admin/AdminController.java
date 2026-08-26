package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.AdminLoginDTO;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员控制器，处理管理员相关的请求，包括登录、创建商家账号等功能。
 */
@Tag(name = "管理端-登录", description = "管理员登录")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 管理员登录接口
     * @param dto 登录请求数据传输对象，包含用户名和密码
     * @return 登录结果，包括生成的JWT令牌（如果登录成功）
     * @throws UnauthorizedException 当用户名或密码不正确时抛出异常
     */
    @Operation(summary = "管理员登录", description = "使用用户名密码登录，返回 JWT Token（登录后请勿在日志中打印密码）")
    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO dto) {
        // 调用服务层的登录方法进行用户验证和返回结果
        return adminUserService.login(dto.getUsername(), dto.getPassword());
    }

}
