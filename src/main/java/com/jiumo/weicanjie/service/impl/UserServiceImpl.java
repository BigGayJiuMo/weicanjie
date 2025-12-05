package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.LoginRequest;
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
    private UserMapper userMapper;

    @Autowired
    private UserStatsService userStatsService;


    /**
     * 手机号登录（统一入口）
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
            return Result.success(user);
        }

        // 未注册 → 自动创建
        User newUser = new User();
        newUser.setPhone(phone);
        newUser.setNickname("手机用户_" + phone.substring(7));
        newUser.setAvatarUrl("/images/default-avatar.png");
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setUpdatedTime(LocalDateTime.now());

        boolean saved = save(newUser);
        if (!saved) {
            return Result.error("注册失败");
        }

        userStatsService.createDefaultStats(newUser.getId());

        return Result.success(newUser);
    }



    /**
     * 按手机号查询
     */
    @Override
    public User getUserByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }


    @Override
    @Transactional
    public Result<User> updateUserInfo(User user) {
        if (user.getId() == null) return Result.error("用户ID不能为空");

        User existing = getById(user.getId());
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

        existing.setUpdatedTime(LocalDateTime.now());
        updateById(existing);

        return Result.success(existing);
    }


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

        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);

        return Result.success(user);
    }


    /**
     * 绑定手机号（允许修改）
     */
    @Override
    @Transactional
    public Result<String> bindPhone(Long userId, String phone) {

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        // 查是否被其他人绑定
        User existed = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .ne(User::getId, userId));

        if (existed != null) {
            return Result.error("该手机号已被其他账号绑定");
        }

        User user = getById(userId);
        if (user == null) return Result.error("用户不存在");

        user.setPhone(phone);
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);

        return Result.success("绑定成功");
    }

    @Override
    @Transactional
    public Result<User> loginByWeChat(String code, LoginRequest.UserInfo userInfo) {

        String openid = "mock_openid_" + code.hashCode();

        User user = getUserByOpenid(openid);

        if (user != null) {
            return Result.success(user);
        }

        // 没有 → 创建新微信用户
        User newUser = new User();
        newUser.setOpenid(openid);
        newUser.setNickname(userInfo != null ? userInfo.getNickname() : "微信用户");
        newUser.setAvatarUrl(userInfo != null ? userInfo.getAvatarUrl() : "/images/default-avatar.png");
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setUpdatedTime(LocalDateTime.now());

        save(newUser);
        userStatsService.createDefaultStats(newUser.getId());

        return Result.success(newUser);
    }

    @Override
    @Transactional
    public Result<String> bindWeChat(Long userId, String openid) {

        if (openid == null || openid.isEmpty()) {
            return Result.error("openid 不能为空");
        }

        // 是否被其他用户绑定
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
        user.setUpdatedTime(LocalDateTime.now());
        updateById(user);

        return Result.success("绑定成功");
    }

    @Override
    public User getUserByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
    }
}
