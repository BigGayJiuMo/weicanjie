package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.common.Result;

public interface UserService extends IService<User> {

    /**
     * 微信登录（重写为支持手机号）
     * @param code 微信登录code
     * @param userInfo 用户信息
     * @param phone 手机号（可选）
     * @return 用户信息
     */
    Result<User> wechatLogin(String code, LoginRequest.UserInfo userInfo, String phone);

    /**
     * 手机号登录
     * @param phone 手机号
     * @return 用户信息
     */
    Result<User> loginByPhone(String phone);

    /**
     * 注册或登录（整合接口）
     * @param code 微信登录code
     * @param userInfo 用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    Result<User> registerOrLogin(String code, LoginRequest.UserInfo userInfo, String phone);

    /**
     * 根据openid获取用户
     * @param openid 微信openid
     * @return 用户信息
     */
    User getUserByOpenid(String openid);

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
     * 通过微信授权码绑定手机号
     * @param userId 用户ID
     * @param code 微信手机号授权码
     * @return 绑定结果
     */
    Result<String> bindPhoneByCode(Long userId, String code);
}