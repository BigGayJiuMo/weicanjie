package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;

public interface AdminService {

    Result<?> login(String username, String password);

    Result<?> createMerchant(AdminUser user);   // 新增商家账号
}
