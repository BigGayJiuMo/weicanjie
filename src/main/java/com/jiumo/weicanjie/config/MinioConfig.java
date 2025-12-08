package com.jiumo.weicanjie.config;

import org.springframework.beans.factory.annotation.Value;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类，用于配置和初始化 MinIO 客户端。
 * MinIO 是一个高性能的分布式对象存储服务，兼容 Amazon S3 API。
 */
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;  // MinIO 服务的端点（URL）

    @Value("${minio.accessKey}")
    private String accessKey;  // MinIO 的访问密钥

    @Value("${minio.secretKey}")
    private String secretKey;  // MinIO 的私密密钥

    /**
     * 初始化并返回 MinIO 客户端。
     * 通过 MinIO 的 endpoint、accessKey 和 secretKey 配置客户端连接。
     *
     * @return 返回一个已配置的 MinioClient 实例
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)  // 设置 MinIO 服务的端点
                .credentials(accessKey, secretKey)  // 设置访问密钥和私密密钥
                .build();  // 构建并返回 MinioClient 实例
    }
}
