package com.jiumo.weicanjie.config.interceptor;

import com.jiumo.weicanjie.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.*;

/**
 * 管理员 JWT 验证拦截器
 */
@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 允许预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        // 读取 Authorization
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        // Bearer token 处理
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 解析 JWT
            Claims claims = jwtUtil.parseToken(token);

            // ⚠ 核心修复：把 token 中的 uid 转为 userId（避免 Controller 取不到）
            Object uid = claims.get("uid");
            if (uid instanceof Integer) {
                request.setAttribute("userId", ((Integer) uid).longValue());
            } else {
                request.setAttribute("userId", uid);
            }

            // 设置角色
            request.setAttribute("role", claims.get("role"));

            // 处理餐厅 ID
            Object rid = claims.get("restaurantId");
            if (rid instanceof Integer) {
                request.setAttribute("restaurantId", ((Integer) rid).longValue());
            } else if (rid instanceof Long) {
                request.setAttribute("restaurantId", rid);
            } else {
                request.setAttribute("restaurantId", null);
            }

            return true;

        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
