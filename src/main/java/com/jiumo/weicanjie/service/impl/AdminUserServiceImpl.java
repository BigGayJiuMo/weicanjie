package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.mapper.AdminUserMapper;
import com.jiumo.weicanjie.service.AdminUserService;
import com.jiumo.weicanjie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminUserServiceImpl 实现类，用于管理后台用户相关操作，如登录、创建账户、删除账户等。
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 用户登录方法，验证用户名和密码，成功后返回用户信息（在拦截器中处理JWT认证）。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含用户信息）
     */
    @Override
    public Result<?> login(String username, String password) {
        // 根据用户名查询用户信息
        AdminUser user = adminUserMapper.selectByUsername(username);
        if (user == null) return Result.error("账号不存在");

        // 校验密码
        if (!encoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        // 生成 JWT Token
        String token = jwtUtil.createToken(user);

        // 构建返回的数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);  // 将 token 添加到响应中
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("restaurantId", user.getRestaurantId());

        return Result.success("登录成功", data);
    }

    /**
     * 创建后台用户账号（商家或厨房角色）。
     *
     * @param user 新用户对象（包含用户名、密码、角色等信息）
     * @return 创建结果
     */
    @Override
    public Result<?> createAccount(AdminUser user) {
        // 判断账号是否已经存在
        if (adminUserMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("账号已存在");
        }

        // 检查是否绑定餐厅
        if (user.getRestaurantId() == null) {
            return Result.error("必须绑定餐厅");
        }

        // 检查角色是否合法
        if (user.getRole() == null ||
                (!"merchant".equals(user.getRole()) && !"kitchen".equals(user.getRole()))) {
            return Result.error("必须选择账号类型：merchant / kitchen");
        }

        // 对密码进行加密
        user.setPassword(encoder.encode(user.getPassword()));

        // 插入新账号
        adminUserMapper.insertAccount(user);

        return Result.success("创建成功");
    }

    /**
     * 查询所有后台账号（商家和厨房角色）。
     *
     * @return 所有账号的列表
     */
    @Override
    public Result<?> listAccounts() {
        return Result.success(adminUserMapper.selectAllAccounts());
    }

    /**
     * 删除指定商家的账号。
     *
     * @param id 商家账号ID
     * @return 删除结果
     */
    @Override
    public Result<?> deleteMerchant(Long id) {
        int rows = adminUserMapper.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 重置商家密码为默认值（123456）。
     *
     * @param id 商家账号ID
     * @return 重置结果
     */
    @Override
    public Result<?> resetPassword(Long id) {
        // 设置默认密码
        String newPwd = encoder.encode("123456");

        // 执行密码重置操作
        int rows = adminUserMapper.resetPassword(id, newPwd);
        return rows > 0 ? Result.success("密码已重置为123456") : Result.error("操作失败");
    }
}
