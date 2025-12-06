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

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Result<?> login(String username, String password) {
        AdminUser user = adminUserMapper.selectByUsername(username);
        if (user == null) return Result.error("账号不存在");

        if (!encoder.matches(password, user.getPassword()))
            return Result.error("密码错误");

        String token = jwtUtil.createToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("restaurantId", user.getRestaurantId());

        return Result.success("登录成功", data);
    }

    /** 创建账号（merchant/kitchen） */
    @Override
    public Result<?> createAccount(AdminUser user) {

        if (adminUserMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("账号已存在");
        }

        if (user.getRestaurantId() == null) {
            return Result.error("必须绑定餐厅");
        }

        if (user.getRole() == null ||
                (!"merchant".equals(user.getRole()) && !"kitchen".equals(user.getRole()))) {
            return Result.error("必须选择账号类型：merchant / kitchen");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        adminUserMapper.insertAccount(user);

        return Result.success("创建成功");
    }

    /** 查询所有账号（merchant + kitchen） */
    @Override
    public Result<?> listAccounts() {
        return Result.success(adminUserMapper.selectAllAccounts());
    }

    @Override
    public Result<?> deleteMerchant(Long id) {
        int rows = adminUserMapper.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    @Override
    public Result<?> resetPassword(Long id) {
        String newPwd = encoder.encode("123456");
        int rows = adminUserMapper.resetPassword(id, newPwd);
        return rows > 0 ? Result.success("密码已重置为123456") : Result.error("操作失败");
    }
}
