package com.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weicanjie.common.Result;
import com.weicanjie.entity.LoginRequest;
import com.weicanjie.entity.User;
import com.weicanjie.mapper.UserMapper;
import com.weicanjie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result<User> wechatLogin(String code, LoginRequest.UserInfo requestUserInfo) {
        try {
            log.info("微信登录，code: {}", code);

            // 生成模拟的openid（开发环境使用）
            String openid = generateMockOpenid(code);

            // 检查用户是否已存在
            User existingUser = getUserByOpenid(openid);

            if (existingUser != null) {
                log.info("用户已存在，更新用户信息: {}", existingUser.getNickname());
                // 更新用户信息
                if (requestUserInfo != null) {
                    existingUser.setNickname(requestUserInfo.getNickname());
                    existingUser.setAvatarUrl(requestUserInfo.getAvatarUrl());
                    existingUser.setUpdatedTime(LocalDateTime.now());
                    updateById(existingUser);
                }
                return Result.success(existingUser);
            } else {
                // 新用户，自动注册
                User newUser = new User();
                newUser.setOpenid(openid);

                if (requestUserInfo != null) {
                    newUser.setNickname(requestUserInfo.getNickname());
                    newUser.setAvatarUrl(requestUserInfo.getAvatarUrl());
                } else {
                    newUser.setNickname("微信用户_" + System.currentTimeMillis() % 10000);
                    newUser.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132");
                }

                newUser.setCreatedTime(LocalDateTime.now());
                newUser.setUpdatedTime(LocalDateTime.now());

                boolean saved = save(newUser);
                if (saved) {
                    log.info("新用户注册成功: {}", newUser.getNickname());
                    return Result.success(newUser);
                } else {
                    log.error("用户注册失败");
                    return Result.error("登录失败");
                }
            }
        } catch (Exception e) {
            log.error("微信登录异常", e);
            return Result.error("登录异常: " + e.getMessage());
        }
    }

    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenid, openid);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public Result<User> updateUserInfo(User user) {
        try {
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }

            User existingUser = getById(user.getId());
            if (existingUser == null) {
                return Result.error("用户不存在");
            }

            // 更新允许修改的字段
            if (user.getNickname() != null) {
                existingUser.setNickname(user.getNickname());
            }
            if (user.getAvatarUrl() != null) {
                existingUser.setAvatarUrl(user.getAvatarUrl());
            }
            if (user.getPhone() != null) {
                existingUser.setPhone(user.getPhone());
            }
            existingUser.setUpdatedTime(LocalDateTime.now());

            boolean updated = updateById(existingUser);
            if (updated) {
                return Result.success(existingUser);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息异常", e);
            return Result.error("更新异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<String> bindPhone(Long userId, String phone) {
        try {
            // 简单的手机号格式验证
            if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
                return Result.error("手机号格式不正确");
            }

            // 检查手机号是否已被绑定
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone)
                    .ne(User::getId, userId);
            User existingUser = getOne(queryWrapper);
            if (existingUser != null) {
                return Result.error("该手机号已被其他账号绑定");
            }

            // 绑定手机号
            User user = getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            user.setPhone(phone);
            user.setUpdatedTime(LocalDateTime.now());
            boolean updated = updateById(user);

            if (updated) {
                return Result.success("绑定成功");
            } else {
                return Result.error("绑定失败");
            }
        } catch (Exception e) {
            log.error("绑定手机号异常", e);
            return Result.error("绑定异常: " + e.getMessage());
        }
    }

    /**
     * 生成模拟的openid（开发环境使用）
     * @param code 微信code
     * @return 模拟的openid
     */
    private String generateMockOpenid(String code) {
        // 在实际项目中，这里应该调用微信API:
        // https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=CODE&grant_type=authorization_code

        // 开发环境模拟：使用code + 随机数生成模拟openid
        return "mock_openid_" + code.hashCode() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}