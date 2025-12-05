package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private AdminUserService adminService;

    @GetMapping("/list")
    public Result<?> list() {
        return adminService.listMerchants();
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody AdminUser user) {
        return adminService.createMerchant(user);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return adminService.deleteMerchant(id);
    }

    @PostMapping("/resetPassword/{id}")
    public Result<?> reset(@PathVariable Long id) {
        return adminService.resetPassword(id);
    }
}