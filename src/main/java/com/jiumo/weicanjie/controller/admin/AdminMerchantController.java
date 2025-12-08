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

    /**
     * 创建一个新的商家或厨房账号
     * @param user 要创建的商家或厨房账号信息
     * @return 操作结果，包含创建状态
     * @note 支持创建商家（merchant）或厨房（kitchen）类型账号
     */
    @PostMapping("/add")   // 前端调用的路径
    public Result<?> add(@RequestBody AdminUser user) {
        return adminUserService.createAccount(user);
    }

    /**
     * 获取所有商家和厨房账号的列表
     * @return 商家和厨房账号列表
     * @note 该接口返回所有类型为 merchant 和 kitchen 的账号
     */
    @GetMapping("/list")
    public Result<?> list() {
        return adminUserService.listAccounts();
    }

    /**
     * 删除指定的商家或厨房账号
     * @param id 要删除的商家或厨房账号的ID
     * @return 操作结果，包含删除状态
     * @note 删除操作会彻底移除指定的商家或厨房账号
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return adminUserService.deleteMerchant(id);
    }

    /**
     * 重置指定商家或厨房账号的密码
     * @param id 需要重置密码的账号ID
     * @return 操作结果，包含重置状态
     * @note 该操作会重置账号的密码为默认值
     */
    @PostMapping("/resetPassword/{id}")
    public Result<?> reset(@PathVariable Long id) {
        return adminUserService.resetPassword(id);
    }
}
