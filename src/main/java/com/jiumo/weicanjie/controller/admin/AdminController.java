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

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO dto) {
        System.out.println("DTO = " + dto);
        System.out.println("username=" + dto.getUsername());
        System.out.println("password=" + dto.getPassword());
        return adminUserService.login(dto.getUsername(), dto.getPassword());
    }

    @PostMapping("/createMerchant")
    public Result<?> createMerchant(@RequestBody AdminUser user) {
        return adminUserService.createMerchant(user);
    }

}
