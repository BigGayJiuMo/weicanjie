package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.AdminLoginDTO;
import com.jiumo.weicanjie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO dto) {
        System.out.println("DTO = " + dto);
        System.out.println("username=" + dto.getUsername());
        System.out.println("password=" + dto.getPassword());
        return adminService.login(dto.getUsername(), dto.getPassword());
    }
}
