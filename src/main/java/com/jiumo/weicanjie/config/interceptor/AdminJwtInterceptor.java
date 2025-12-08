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
            token = token.substring(7);
        }

        try {
            Claims claims = jwtUtil.parseToken(token);

            request.setAttribute("uid", claims.get("uid"));
            request.setAttribute("role", claims.get("role"));

            // 修复 Integer 无法转 Long 的问题
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
