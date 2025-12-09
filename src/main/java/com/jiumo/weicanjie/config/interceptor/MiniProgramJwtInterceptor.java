package com.jiumo.weicanjie.config.interceptor;

import com.jiumo.weicanjie.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.*;

/**
 * 小程序端 JWT 验证拦截器，负责对小程序端请求进行 JWT 认证。
 * 在请求到达控制器之前，解析请求中的 JWT token，验证其有效性，
 * 并将解析出的用户信息（如用户ID、角色、餐厅ID）存入请求属性中。
 */
@Component
public class MiniProgramJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;  // 注入自定义的 JWT 工具类，用于解析和验证 JWT

    /**
     * 在处理请求之前进行 JWT 校验。
     * 如果请求包含有效的 JWT token，将会将用户信息存入请求属性，供后续操作使用。
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return 返回 true 允许继续处理请求，返回 false 拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 允许预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        // 获取请求头中的 Authorization 字段，通常为 "Bearer <token>"
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);  // 未提供 token，返回 401 错误
            return false;
        }

        // 去除 "Bearer " 前缀，提取 token
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute("uid", claims.get("uid"));
            request.setAttribute("role", claims.get("role"));

            // 打印调试日志，确保用户信息被正确解析
            System.out.println("JWT 校验通过，用户ID: " + claims.get("uid"));
            return true;
        } catch (Exception e) {
            response.setStatus(401);  // token 无效或解析失败，返回 401 错误
            return false;
        }
    }
}
