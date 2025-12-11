package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.AdminUser;
import com.jiumo.weicanjie.mapper.AdminUserMapper;
import com.jiumo.weicanjie.service.AdminUserService;
import com.jiumo.weicanjie.util.JwtUtil;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * AdminUserServiceImpl 实现类，用于管理后台用户相关操作，如登录、创建账户、删除账户等。
 */
@Service
public class AdminUserServiceImpl
        extends ServiceImpl<AdminUserMapper, AdminUser>
        implements AdminUserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redis;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Result<?> login(String username, String password) {

        AdminUser user = lambdaQuery()
                .eq(AdminUser::getUsername, username)
                .one();

        if (user == null) return Result.error("账号不存在");

        if (!encoder.matches(password, user.getPassword()))
            return Result.error("密码错误");

        String token = jwtUtil.createToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("restaurantId", user.getRestaurantId());
        data.put("id", user.getId());
        data.put("phone", user.getPhone());

        return Result.success("登录成功", data);
    }

    @Override
    public Result<?> createAccount(AdminUser user) {

        boolean exist = lambdaQuery()
                .eq(AdminUser::getUsername, user.getUsername())
                .exists();

        if (exist) return Result.error("账号已存在");

        if (user.getRestaurantId() == null)
            return Result.error("必须绑定餐厅");

        if (!"merchant".equals(user.getRole()) && !"kitchen".equals(user.getRole()))
            return Result.error("必须选择 merchant 或 kitchen");

        user.setPassword(encoder.encode(user.getPassword()));

        save(user); // MP 内置插入

        return Result.success("创建成功");
    }

    @Override
    public Result<?> listAccounts(String keyword) {

        var list = lambdaQuery()
                .in(AdminUser::getRole, "merchant", "kitchen")
                .like(keyword != null && !keyword.isEmpty(),
                        AdminUser::getUsername, keyword)
                .orderByDesc(AdminUser::getId)
                .list();

        return Result.success(list);
    }

    @Override
    public Result<?> deleteMerchant(Long id) {

        boolean ok = removeById(id);

        return ok ? Result.success("删除成功")
                : Result.error("删除失败");
    }

    @Override
    public Result<?> resetPassword(Long id) {

        boolean ok = lambdaUpdate()
                .eq(AdminUser::getId, id)
                .set(AdminUser::getPassword, encoder.encode("123456"))
                .update();

        return ok ? Result.success("密码已重置为123456")
                : Result.error("操作失败");
    }

    @Override
    public Result<?> sendCode(String phone) {

        String code = String.format("%06d", new Random().nextInt(999999));

        String key = "admin:code:" + phone;

        redis.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        Map<String, Object> map = new HashMap<>();
        map.put("code", code);

        return Result.success("验证码已发送（模拟）", map);
    }

    @Override
    public Result<?> updatePasswordWithCode(Long id, String code, String newPwd, String phone) {

        String real = redis.opsForValue().get("admin:code:" + phone);

        if (real == null || !real.equals(code))
            return Result.error("验证码错误或已过期");

        lambdaUpdate()
                .eq(AdminUser::getId, id)
                .set(AdminUser::getPassword, encoder.encode(newPwd))
                .update();

        redis.delete("admin:code:" + phone);

        return Result.success("密码修改成功");
    }

    @Override
    public Result<?> bindPhoneWithCode(Long id, String phone, String code) {

        String real = redis.opsForValue().get("admin:code:" + phone);

        if (real == null || !real.equals(code))
            return Result.error("验证码错误或已过期");

        boolean used = lambdaQuery()
                .eq(AdminUser::getPhone, phone)
                .exists();

        if (used) return Result.error("手机号已被使用");

        lambdaUpdate()
                .eq(AdminUser::getId, id)
                .set(AdminUser::getPhone, phone)
                .update();

        redis.delete("admin:code:" + phone);

        return Result.success("手机号绑定成功");
    }
}
