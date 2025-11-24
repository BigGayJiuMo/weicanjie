package com.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weicanjie.entity.LoginRequest;
import com.weicanjie.entity.User;
import com.weicanjie.common.Result;

public interface UserService extends IService<User> {

    /**
     * 微信登录
     * @param code 微信登录code
     * @return 用户信息
     */
    Result<User> wechatLogin(String code, LoginRequest.UserInfo userInfo);

    /**
     * 根据openid获取用户
     * @param openid 微信openid
     * @return 用户信息
     */
    User getUserByOpenid(String openid);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    Result<User> updateUserInfo(User user);

    /**
     * 绑定手机号
     * @param userId 用户ID
     * @param phone 手机号
     * @return 绑定结果
     */
    Result<String> bindPhone(Long userId, String phone);
}