package com.jiumo.weicanjie.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类，用于配置 MyBatis Plus 插件。
 * 该类主要配置分页插件（PaginationInnerInterceptor），并将其注册到 MyBatis Plus 的拦截器中。
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置 MyBatis Plus 拦截器。
     * 通过添加分页拦截器来支持数据库分页查询功能。
     * 该方法通过 MyBatis Plus 插件的拦截器机制，为 MyBatis 添加分页功能。
     *
     * @return 返回配置好的 MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页拦截器，指定数据库类型为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
