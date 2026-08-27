package com.jiumo.weicanjie.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
 * - value 使用 JSON 存储（携带 {@code @class} 类型标记，反序列化才能还原为目标类型）
 * - 默认 TTL 5 分钟
 * <p>
 * 注意（重要坑）：
 * 1. {@code GenericJackson2JsonRedisSerializer(ObjectMapper)} 传自定义 ObjectMapper 的构造
 *    方法【不会】自动启用 Jackson 类型标记，序列化结果没有 {@code @class}，读取时只能反序列化成
 *    {@code LinkedHashMap}，Spring Cache 强转方法返回类型（如 Result）会抛 ClassCastException！
 *    所以这里必须手动调用 {@code activateDefaultTyping(...)}。
 * 2. Java 8 时间类型（LocalDateTime）必须注册 JavaTimeModule，否则序列化抛异常，
 *    且 Spring Cache 对 put 异常只记日志不中断 → 缓存静默失效。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // 带 JavaTimeModule 且启用类型标记的 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 关键：显式启用默认类型（写 @class），否则反序列化只能得到 LinkedHashMap
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // key: 字符串
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // value: JSON（携带 @class 类型信息，还原目标类型）
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(valueSerializer))
                // 默认过期时间 5 分钟
                .entryTtl(Duration.ofMinutes(5));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
