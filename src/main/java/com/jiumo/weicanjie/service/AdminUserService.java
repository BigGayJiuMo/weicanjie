package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;

public interface AdminUserService {

    Result<?> login(String username, String password);

    Result<?> createMerchant(AdminUser user);

    Result<?> listMerchants();  // 获取商家账号列表

    Result<?> deleteMerchant(Long id);  // 删除商家账号

    Result<?> resetPassword(Long id);  // 重置密码
}
