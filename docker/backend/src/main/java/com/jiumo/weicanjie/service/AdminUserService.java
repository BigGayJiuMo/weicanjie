package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;

/**
 * AdminUserService 接口，定义了管理员相关操作，包括登录、创建账号、删除账号等。
 */
public interface AdminUserService extends IService<AdminUser> {

    /**
     * 管理员用户登录
     * @param username 管理员用户名
     * @param password 管理员密码
     * @return 登录结果，成功返回用户信息，失败返回错误信息
     */
    Result<?> login(String username, String password);

    /**
     * 创建新的管理员账号
     * @param user 新管理员用户对象
     * @return 账户创建结果，成功返回成功信息，失败返回错误信息
     */
    Result<?> createAccount(AdminUser user);

    /**
     * 获取所有管理员账号列表
     * @return 包含所有管理员、商家和后厨的账号列表
     */
    Result<?> listAccounts(String keyword);

    /**
     * 删除商家账号
     * @param id 商家账号ID
     * @return 删除操作结果，成功返回成功信息，失败返回错误信息
     */
    Result<?> deleteMerchant(Long id);

    /**
     * 重置管理员账户密码
     * @param id 管理员账号ID
     * @return 密码重置操作结果，成功返回成功信息，失败返回错误信息
     */
    Result<?> resetPassword(Long id);

    Result<?> sendCode(String phone);

    Result<?> updatePasswordWithCode(Long id, String code, String newPwd, String phone);

    Result<?> bindPhoneWithCode(Long id, String phone, String code);
}
