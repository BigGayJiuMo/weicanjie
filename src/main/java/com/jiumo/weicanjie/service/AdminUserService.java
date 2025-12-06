package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;

public interface AdminUserService {

    Result<?> login(String username, String password);

    Result<?> createAccount(AdminUser user); // 新方法（取代 createMerchant）

    Result<?> listAccounts();  // 查询所有账号（merchant + kitchen）

    Result<?> deleteMerchant(Long id);

    Result<?> resetPassword(Long id);
}
