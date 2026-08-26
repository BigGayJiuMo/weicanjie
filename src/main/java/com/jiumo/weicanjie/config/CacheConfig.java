package com.jiumo.weicanjie.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 缓存配置（Spring Cache 抽象）
 * <p>
 * - key 使用 String 序列化（可读性好，便于排查）
 * - value 使用 GenericJackson2JsonRedisSerializer（JSON 存储，携带类型信息，避免 JDK 序列化乱码）
 * - 默认 TTL 5 分钟
 * <p>
 * 注意：GenericJackson2JsonRedisSerializer 默认的 ObjectMapper 不支持 Java 8 时间类型
 * （LocalDateTime 等），必须手动注册 JavaTimeModule，否则缓存写入会 SerializationException
 * （Spring 只记日志不报错，导致缓存静默失效——压测"带缓存"和"无缓存"几乎一样慢就是这个原因）。
 * <p>
 * 面试点：
 * 1. 为什么用 JSON 不用 JDK 序列化？—— JDK 序列化可读性差、体积大、且要求类实现 Serializable；
 * 2. 缓存一致性策略：先更新数据库，再删除缓存（Cache Aside 模式），读时 miss 则回源 DB 并回填。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // 支持 LocalDateTime 的 ObjectMapper（注册 JavaTimeModule）
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // key: 字符串
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // value: JSON（携带 @class 类型信息，反序列化不丢类型；支持 LocalDateTime）
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(valueSerializer))
                // 默认过期时间 5 分钟
                .entryTtl(Duration.ofMinutes(5));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
