package com.jiumo.weicanjie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文档配置（Knife4j 界面由 knife4j-openapi3-spring-boot-starter 提供）
 * <p>
 * 访问地址（注意 context-path 是 /api）：
 * - 文档页面: http://localhost:8080/api/doc.html
 * - OpenAPI JSON: http://localhost:8080/api/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("微餐捷餐厅管理系统 API")
                .description("用户端 + 商家端 + 管理后台 + 后厨端 REST API\n\n"
                        + "接口前缀: /api\n"
                        + "鉴权说明: 管理端接口需携带 JWT Token(请求头 Authorization: Bearer xxx)")
                .version("1.0.0")
                .contact(new Contact().name("吴锦釬")));
    }
}
