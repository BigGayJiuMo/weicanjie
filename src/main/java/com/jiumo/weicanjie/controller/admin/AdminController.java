package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.AdminLoginDTO;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员控制器，处理管理员相关的请求，包括登录、创建商家账号等功能。
 */
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
    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO dto) {
        // 打印登录请求信息，用于调试
        System.out.println("DTO = " + dto);
        System.out.println("username=" + dto.getUsername());
        System.out.println("password=" + dto.getPassword());

        // 调用服务层的登录方法进行用户验证和返回结果
        return adminUserService.login(dto.getUsername(), dto.getPassword());
    }

    /**
     * 创建商家账户接口
     * @param user 新建商家用户的详细信息（用户名、密码等）
     * @return 操作结果，包括是否创建成功
     * @throws InvalidInputException 当输入的数据不合法时抛出异常
     * @throws UserAlreadyExistsException 当用户名已存在时抛出异常
     */
    @PostMapping("/createMerchant")
    public Result<?> createMerchant(@RequestBody AdminUser user) {
        // 调用服务层方法创建商家账户
        return adminUserService.createAccount(user);
    }

}
