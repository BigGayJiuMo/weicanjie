package com.jiumo.weicanjie.service.impl;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.mapper.AdminUserMapper;
import com.jiumo.weicanjie.service.AdminService;
import com.jiumo.weicanjie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Result<?> login(String username, String password) {

        AdminUser user = adminUserMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("账号不存在");
        }

        if (!encoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.createToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("restaurantId", user.getRestaurantId());

        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> createMerchant(AdminUser user) {

        // 账号是否存在
        if (adminUserMapper.selectByUsername(user.getUsername()) != null) {
            return Result.error("账号已存在");
        }

        if (user.getRestaurantId() == null) {
            return Result.error("必须选择商家绑定的餐厅");
        }

        // 加密密码
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("merchant");

        adminUserMapper.insertMerchant(user);

        return Result.success("创建成功");
    }
}
