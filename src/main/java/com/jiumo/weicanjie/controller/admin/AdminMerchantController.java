package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private AdminUserService adminUserService;

    /** 创建账号（支持 merchant/kitchen） */
    @PostMapping("/add")   // ⭐ 前端调用的路径
    public Result<?> add(@RequestBody AdminUser user) {
        return adminUserService.createAccount(user);
    }

    /** 获取所有 merchant + kitchen */
    @GetMapping("/list")
    public Result<?> list() {
        return adminUserService.listAccounts();
    }

    /** 删除 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return adminUserService.deleteMerchant(id);
    }

    /** 重置密码 */
    @PostMapping("/resetPassword/{id}")
    public Result<?> reset(@PathVariable Long id) {
        return adminUserService.resetPassword(id);
    }
}
