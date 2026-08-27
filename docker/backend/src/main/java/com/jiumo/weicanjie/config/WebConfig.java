package com.jiumo.weicanjie.config;

import com.jiumo.weicanjie.config.interceptor.AdminJwtInterceptor;
import com.jiumo.weicanjie.config.interceptor.MiniProgramJwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web 配置类，用于配置 Spring MVC 的拦截器。
 * 主要功能是为后台管理接口添加 JWT 验证拦截器，确保后台请求的安全性。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminJwtInterceptor adminJwtInterceptor;  // 注入自定义的 JWT 拦截器

    @Autowired
    private MiniProgramJwtInterceptor miniProgramJwtInterceptor;
    /**
     * 配置 Spring MVC 的拦截器。
     * 注册自定义的 JWT 拦截器，拦截所有后台管理接口（/admin/**），
     * 除了登录接口（/admin/login），避免拦截登录请求。
     *
     * @param registry 拦截器注册器，用于添加拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminJwtInterceptor)  // 添加自定义的 JWT 拦截器
                .addPathPatterns("/admin/**")  // 拦截所有 /admin/** 路径的请求
                .excludePathPatterns("/admin/login");  // 排除 /admin/login 路径，不进行 JWT 验证
        // 注册小程序端的 JWT 拦截器
        registry.addInterceptor(miniProgramJwtInterceptor)
                .addPathPatterns("/user/**")  // 拦截所有 /user/** 路径的请求
                .excludePathPatterns("/user/loginByPhone", "/user/loginByWeChat");  // 排除登录接口

    }
}
