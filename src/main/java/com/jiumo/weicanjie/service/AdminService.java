package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.mapper.AdminUserMapper;
import com.jiumo.weicanjie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public Result<?> login(String username, String password) {
        AdminUser user = adminUserMapper.selectByUsername(username);
        if (user == null) return Result.error("账号不存在");

        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        String token = jwtUtil.createToken(user.getId());

        // JDK8 写法
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        return Result.success(data);
    }
}
