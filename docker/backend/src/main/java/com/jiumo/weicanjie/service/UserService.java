package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.dto.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.common.Result;

import java.util.HashMap;

/**
 * 用户服务接口，提供用户相关的业务逻辑操作。
 * 该接口继承自 MyBatis Plus 的 IService 接口，包含了对用户信息的增、查、改、删操作。
 */
public interface UserService extends IService<User> {

    /**
     * 手机号登录
     *
     * @param phone 手机号
     * @return 返回登录的用户信息
     */
    Result<HashMap<String, Object>> loginByPhone(String phone);

    /**
     * 根据手机号获取用户信息
     *
     * @param phone 手机号
     * @return 返回匹配的用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 返回更新结果
     */
    Result<User> updateUserInfo(User user);

    /**
     * 更新用户资料（头像和昵称）
     *
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatarUrl 头像URL
     * @return 返回更新结果
     */
    Result<User> updateUserProfile(Long userId, String nickname, String avatarUrl);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param phone 手机号
     * @return 返回绑定结果
     */
    Result<String> bindPhone(Long userId, String phone);

    /**
     * 根据微信 OpenID 获取用户信息
     *
     * @param openid 微信 OpenID
     * @return 返回匹配的用户信息
     */
    User getUserByOpenid(String openid);

    /**
     * 微信登录
     *
     * @param code 微信登录授权码
     * @param userInfo 微信用户信息
     * @return 返回登录的用户信息
     */
    Result<HashMap<String, Object>> loginByWeChat(String code, LoginRequest.UserInfo userInfo);

    /**
     * 微信绑定
     *
     * @param userId 用户ID
     * @param openid 微信 OpenID
     * @return 返回绑定结果
     */
    Result<String> bindWeChat(Long userId, String openid);
}
