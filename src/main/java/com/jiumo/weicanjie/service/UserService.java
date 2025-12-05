package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.common.Result;

public interface UserService extends IService<User> {

    /**
     * 手机号登录
     * @param phone 手机号
     * @return 用户信息
     */
    Result<User> loginByPhone(String phone);

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    Result<User> updateUserInfo(User user);

    /**
     * 更新用户资料（头像和昵称）
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    Result<User> updateUserProfile(Long userId, String nickname, String avatarUrl);

    /**
     * 绑定手机号
     * @param userId 用户ID
     * @param phone 手机号
     * @return 绑定结果
     */
    Result<String> bindPhone(Long userId, String phone);

    /**
     * 微信open_id
     */
    User getUserByOpenid(String openid);
    /**
     * 微信登录
     */
    Result<User> loginByWeChat(String code, LoginRequest.UserInfo userInfo);

    /**
     * 微信绑定
     */
    Result<String> bindWeChat(Long userId, String openid);

}