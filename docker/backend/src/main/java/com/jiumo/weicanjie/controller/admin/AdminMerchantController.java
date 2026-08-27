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
    @PostMapping("/add")
    public Result<?> add(@RequestBody AdminUser user, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限创建账号");
        }
        return adminUserService.createAccount(user);
    }

    /**
     * 获取所有商家和厨房账号的列表
     * @return 商家和厨房账号列表
     * @note 该接口返回所有类型为 merchant 和 kitchen 的账号
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String keyword) {
        return adminUserService.listAccounts(keyword);
    }

    /**
     * 删除指定的商家或厨房账号
     * @param id 要删除的商家或厨房账号的ID
     * @return 操作结果，包含删除状态
     * @note 删除操作会彻底移除指定的商家或厨房账号
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限执行此操作");
        }
        return adminUserService.deleteMerchant(id);
    }

    /**
     * 重置指定商家或厨房账号的密码
     * @param id 需要重置密码的账号ID
     * @return 操作结果，包含重置状态
     * @note 该操作会重置账号的密码为默认值
     */
    @PostMapping("/resetPassword/{id}")
    public Result<?> reset(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"super".equals(role)) {
            return Result.error("无权限执行此操作");
        }
        return adminUserService.resetPassword(id);
    }

    /**
     * 发送验证码（模拟）
     */
    @PostMapping("/sendCode")
    public Result<?> sendCode(@RequestParam String phone) {
        return adminUserService.sendCode(phone);
    }

    /**
     * 验证码修改密码（商家自己用）
     */
    @PostMapping("/changePasswordByCode")
    public Result<?> changePasswordByCode(
            @RequestParam String phone,
            @RequestParam String code,
            @RequestParam String newPwd,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return adminUserService.updatePasswordWithCode(userId, code, newPwd, phone);
    }

    /**
     * 验证码绑定/修改手机号
     */
    @PostMapping("/bindPhoneByCode")
    public Result<?> bindPhoneByCode(
            @RequestParam String phone,
            @RequestParam String code,
            HttpServletRequest request
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return adminUserService.bindPhoneWithCode(userId, phone, code);
    }
}
