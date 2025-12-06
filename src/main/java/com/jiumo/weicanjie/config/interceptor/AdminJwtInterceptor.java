package com.jiumo.weicanjie.config.interceptor;

import com.jiumo.weicanjie.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.*;

@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // 去掉 Bearer 空格
        }

        try {
            Claims claims = jwtUtil.parseToken(token);

            request.setAttribute("uid", claims.get("uid"));
            request.setAttribute("role", claims.get("role"));
            request.setAttribute("restaurantId", claims.get("restaurantId"));

            return true;
        } catch (Exception e) {
            e.printStackTrace(); // ⭐ 临时加上用于调试
            response.setStatus(401);
            return false;
        }

    }
}
