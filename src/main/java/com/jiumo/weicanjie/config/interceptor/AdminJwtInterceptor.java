package com.jiumo.weicanjie.config.interceptor;

import com.jiumo.weicanjie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 从 header 中取 token
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 未登录
            return false;
        }

        try {
            // 校验 token
            jwtUtil.parseToken(token);
            return true;
        } catch (Exception e) {
            response.setStatus(401); // token 失效
            return false;
        }
    }
}
