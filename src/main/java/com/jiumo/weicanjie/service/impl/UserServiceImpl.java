package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.dto.LoginRequest;
import com.jiumo.weicanjie.entity.User;
import com.jiumo.weicanjie.mapper.UserMapper;
import com.jiumo.weicanjie.service.UserService;
import com.jiumo.weicanjie.service.UserStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserStatsService userStatsService;  // 注入 UserStatsService，用于用户统计的相关操作

    /**
     * 手机号登录（统一入口）
     * 该方法首先校验手机号格式，若手机号未注册，则自动创建新用户，并为其创建默认的统计信息。
     *
     * @param phone 用户的手机号码
     * @return 返回登录的用户信息
     */
    @Override
    @Transactional
    public Result<User> loginByPhone(String phone) {

        // 手机号格式校验
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 查询用户
        User user = getUserByPhone(phone);

        if (user != null) {
            return Result.success(user);  // 用户已存在，返回用户信息
        }

        // 未注册 → 自动创建新用户
        User newUser = new User();
        newUser.setPhone(phone);
        newUser.setNickname("手机用户_" + phone.substring(7));  // 设置默认昵称
        newUser.setAvatarUrl("/images/default-avatar.png");  // 设置默认头像
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setUpdatedTime(LocalDateTime.now());

        boolean saved = save(newUser);  // 插入新用户
        if (!saved) {
            return Result.error("注册失败");
        }

        // 创建默认统计信息
        userStatsService.createDefaultStats(newUser.getId());

        return Result.success(newUser);  // 返回新创建的用户信息
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 用户的手机号码
     * @return 返回该手机号对应的用户信息
     */
    @Override
    public User getUserByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));  // 查询数据库中该手机号的用户
    }

    /**
     * 更新用户信息
     *
     * @param user 包含更新信息的 User 对象
     * @return 返回更新后的用户信息
     */
    @Override
    @Transactional
    public Result<User> updateUserInfo(User user) {
        if (user.getId() == null) return Result.error("用户ID不能为空");

        User existing = getById(user.getId());  // 查找原用户
        if (existing == null) return Result.error("用户不存在");

        if (user.getNickname() != null) {
            existing.setNickname(user.getNickname());
        }

        if (user.getAvatarUrl() != null) {
            existing.setAvatarUrl(user.getAvatarUrl());
        }

        if (user.getPhone() != null) {
            User samePhoneUser = getUserByPhone(user.getPhone());
            if (samePhoneUser != null && !samePhoneUser.getId().equals(existing.getId())) {
                return Result.error("该手机号已被其他用户绑定");
            }
            existing.setPhone(user.getPhone());
        }

        existing.setUpdatedTime(LocalDateTime.now());  // 更新用户的更新时间
        updateById(existing);

        return Result.success(existing);  // 返回更新后的用户信息
    }

    /**
     * 更新用户资料（头像和昵称）
     *
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatarUrl 头像URL
     * @return 返回更新后的用户信息
     */
    @Override
    @Transactional
    public Result<User> updateUserProfile(Long userId, String nickname, String avatarUrl) {

        User user = getById(userId);
        if (user == null) return Result.error("用户不存在");

        boolean changed = false;

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
            changed = true;
        }

        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            user.setAvatarUrl(avatarUrl);
            changed = true;
        }

        if (!changed) return Result.error("没有要更新的数据");

        user.setUpdatedTime(LocalDateTime.now());  // 更新用户的更新时间
        updateById(user);

        return Result.success(user);  // 返回更新后的用户资料
    }

    /**
     * 绑定手机号（允许修改）
     *
     * @param userId 用户ID
     * @param phone 手机号
     * @return 返回绑定结果
     */
    @Override
    @Transactional
    public Result<String> bindPhone(Long userId, String phone) {

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 查找手机号是否已被其他用户绑定
        User existed = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .ne(User::getId, userId));

        if (existed != null) {
            return Result.error("该手机号已被其他账号绑定");
        }

        User user = getById(userId);
        if (user == null) return Result.error("用户不存在");

        user.setPhone(phone);
        user.setUpdatedTime(LocalDateTime.now());  // 更新用户的更新时间
        updateById(user);

        return Result.success("绑定成功");
    }

    /**
     * 微信登录
     *
     * @param code 微信授权码
     * @param userInfo 微信用户信息
     * @return 返回登录的用户信息
     */
    @Override
    @Transactional
    public Result<User> loginByWeChat(String code, LoginRequest.UserInfo userInfo) {

        String openid = "mock_openid_" + code.hashCode();  // 模拟通过授权码生成 OpenID

        User user = getUserByOpenid(openid);

        if (user != null) {
            return Result.success(user);  // 微信账号已注册，返回用户信息
        }

        // 创建新的微信用户
        User newUser = new User();
        newUser.setOpenid(openid);
        newUser.setNickname(userInfo != null ? userInfo.getNickname() : "微信用户");
        newUser.setAvatarUrl(userInfo != null ? userInfo.getAvatarUrl() : "/images/default-avatar.png");
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setUpdatedTime(LocalDateTime.now());

        save(newUser);
        userStatsService.createDefaultStats(newUser.getId());  // 创建默认的用户统计信息

        return Result.success(newUser);  // 返回新创建的用户信息
    }

    /**
     * 微信绑定
     *
     * @param userId 用户ID
     * @param openid 微信OpenID
     * @return 返回绑定结果
     */
    @Override
    @Transactional
    public Result<String> bindWeChat(Long userId, String openid) {

        if (openid == null || openid.isEmpty()) {
            return Result.error("openid 不能为空");
        }

        // 检查 OpenID 是否已被其他用户绑定
        User existed = getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
                        .ne(User::getId, userId)
        );

        if (existed != null) {
            return Result.error("该微信账号已被其他用户绑定");
        }

        User user = getById(userId);
        if (user == null) return Result.error("用户不存在");

        user.setOpenid(openid);
        user.setUpdatedTime(LocalDateTime.now());  // 更新用户的更新时间
        updateById(user);

        return Result.success("绑定成功");
    }

    /**
     * 根据微信 OpenID 获取用户信息
     *
     * @param openid 微信 OpenID
     * @return 返回对应的用户信息
     */
    @Override
    public User getUserByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));  // 根据 OpenID 查询用户
    }
}
