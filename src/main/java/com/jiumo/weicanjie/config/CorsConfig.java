package com.jiumo.weicanjie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 跨域配置类（CORS），用于配置 Spring MVC 的跨域访问策略。
 * 允许跨域请求访问所有路径，并设置允许的请求方式、头信息等。
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域访问策略。
     * 通过该方法配置应用的跨域请求支持，允许来自所有源的请求，并指定允许的 HTTP 方法、头信息等。
     *
     * @return WebMvcConfigurer 配置对象，配置跨域访问规则
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            /**
             * 添加跨域映射配置。
             * 通过 CorsRegistry 配置跨域映射规则，允许所有路径（/**）的跨域访问，并设置允许的 HTTP 方法和头信息。
             *
             * @param registry 用于注册跨域映射的 CorsRegistry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // 允许所有路径的跨域请求
                        .allowedOriginPatterns("*")   // 允许所有源进行跨域请求
                        .allowedMethods("*")  // 允许所有 HTTP 方法（GET, POST, PUT, DELETE, etc.）
                        .allowedHeaders("*")  // 允许所有请求头
                        .allowCredentials(true)  // 允许携带凭证（如 Cookies）
                        .maxAge(3600);  // 设置预检请求的缓存时间为 1 小时（3600 秒）
            }
        };
    }
}
