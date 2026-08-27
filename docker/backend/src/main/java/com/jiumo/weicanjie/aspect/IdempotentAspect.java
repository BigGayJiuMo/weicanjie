package com.jiumo.weicanjie.aspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jiumo.weicanjie.annotation.Idempotent;
import com.jiumo.weicanjie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * 幂等切面：拦截标注了 {@link Idempotent} 的 Controller 方法。
 * <p>
 * 核心流程（保证并发下只会有一个请求执行到业务逻辑）：
 * <pre>
 * 请求带 X-Idempotent-Key
 *   ├─ 无 key → 直接放行（老客户端兼容，幂等性降级为不保证）
 *   ├─ SET NX 成功 → 首次请求，执行业务
 *   │     ├─ 业务成功 → 结果缓存 10 分钟，删除锁 → 返回成功
 *   │     └─ 业务失败 → 删除锁（key 释放，下次可重试）→ 返回失败
 *   └─ SET NX 失败 → 前一个请求还在处理（并发重复提交）
 *         ├─ 缓存已有结果 → 返回缓存结果（“重复提交”）
 *         └─ 缓存还没有结果 → 返回 409（另一线程正在执行同一笔业务）
 * </pre>
 * 面试点：
 * <ul>
 *   <li>为什么 SET NX + 结果缓存是双保险？—— 单靠 SET NX 只能挡并发，
 *       挡不住"第一个请求成功但响应丢失后用户手动重试"（锁过期/已删除）；
 *       结果缓存负责挡住时间上的重复。</li>
 *   <li>锁的过期时间为什么必须大于业务执行时间？—— 否则业务没跑完锁就过期，
 *       第二个并发请求会把业务再执行一遍；这里默认 10 分钟，正常订单创建远小于此。</li>
 *   <li>为什么业务失败要删锁？—— 失败意味着这笔操作没成功，用户重试是合理诉求，
 *       应允许他重试；只有成功的结果才缓存起来防重。</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    /** 请求头名称：客户端（小程序/管理端/压测脚本）必须透传 */
    public static final String HEADER_IDEMPOTENT_KEY = "X-Idempotent-Key";

    /** Redis key 前缀（锁 key 与结果 key 分开，删锁不会误删结果缓存） */
    public static final String PREFIX = "idempotent:";
    public static final String LOCK_PREFIX = PREFIX + "lock:";
    public static final String RESULT_PREFIX = PREFIX + "result:";

    /**
     * 结果缓存专用的 ObjectMapper：
     * - 注册 JavaTimeModule（Order.createdTime 是 LocalDateTime，不注册会抛
     *   InvalidDefinitionException，跟之前缓存三坑①是同一个坑！）
     * - 禁写日期为时间戳
     * - 不启用 Jackson 类型标记（@class），因为我们反序列化时只认 Result 的
     *   code/message/data/timestamp 四个字段，够用且更安全。
     */
    private static final ObjectMapper RESULT_MAPPER = new ObjectMapper();
    static {
        RESULT_MAPPER.registerModule(new JavaTimeModule());
        RESULT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Result 的 id 等字段是 private，不设可见性会序列化成空对象
        RESULT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        // 1. 从当前请求取幂等 key（无 key 直接放行，保持老客户端兼容）
        HttpServletRequest request = currentRequest();
        String idempotentKey = request != null ? request.getHeader(HEADER_IDEMPOTENT_KEY) : null;
        if (idempotentKey == null || idempotentKey.isEmpty()) {
            return joinPoint.proceed();
        }

        String lockKey = LOCK_PREFIX + idempotent.prefix() + ":" + idempotentKey;
        String resultKey = RESULT_PREFIX + idempotent.prefix() + ":" + idempotentKey;
        Duration ttl = Duration.ofSeconds(idempotent.expireSeconds());

        // 2. SET NX：只有一个请求能拿到锁
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", ttl);

        if (Boolean.TRUE.equals(locked)) {
            // 2.1 首次请求 → 执行业务
            try {
                // 执行业务前先查一下结果缓存：如果之前已成功缓存过结果（比如缓存还没过期），直接返回
                String preCached = stringRedisTemplate.opsForValue().get(resultKey);
                if (preCached != null && !preCached.isEmpty()) {
                    log.info("幂等已成功过, 直接返回缓存结果: key={}", idempotentKey);
                    return parseResult(preCached);
                }
                Object result = joinPoint.proceed();
                // 业务成功才缓存结果（防的是"已成功"后的重复提交；失败允许重试）
                if (result instanceof Result && ((Result<?>) result).isSuccess()) {
                    stringRedisTemplate.opsForValue().set(resultKey, serialize(result), ttl);
                    log.info("幂等成功, 缓存结果: key={}", idempotentKey);
                }
                return result;
            } finally {
                // 2.2 删除锁（锁与结果缓存分离，删锁不会影响结果缓存）
                stringRedisTemplate.delete(lockKey);
                log.info("幂等锁释放: key={}", idempotentKey);
            }
        } else {
            // 3. 并发/重复请求：拿不到锁
            String cached = stringRedisTemplate.opsForValue().get(resultKey);
            if (cached != null && !cached.isEmpty()) {
                // 3.1 第一次请求已成功并缓存了结果 → 直接返回缓存结果
                log.info("幂等重复提交, 返回缓存结果: key={}", idempotentKey);
                return parseResult(cached);
            }
            // 3.2 第一个请求还在执行中 → 告诉客户端"处理中，请勿重复提交"
            log.warn("幂等并发冲突: key={}", idempotentKey);
            return Result.idempotentConflict();
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String serialize(Object obj) {
        try {
            return RESULT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("幂等结果序列化失败: key={}, err={}", obj, e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Result<?> parseResult(String json) {
        try {
            return RESULT_MAPPER.readValue(json, Result.class);
        } catch (Exception e) {
            log.error("幂等结果反序列化失败: {}", e.getMessage(), e);
            return Result.idempotentConflict();
        }
    }
}