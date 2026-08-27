package com.jiumo.weicanjie.annotation;

import java.lang.annotation.*;

/**
 * 接口幂等注解：标记需要做"防重复提交"的写接口（如创建订单、模拟支付、申请退款）。
 * <p>
 * 原理（Redis 分布式锁 + 结果缓存双保险）：
 * <ol>
 *   <li>请求必须携带请求头 {@code X-Idempotent-Key}（OpenFeign/RestTemplate/小程序 request
 *       均透传）。后台在调用前先 SET NX 加锁：</li>
 *   <ul>
 *     <li>加锁成功 → 首次请求，放行执行业务；</li>
 *     <li>加锁失败 → 说明同一 key 的前一个请求正在处理（并发重复提交），直接返回幂等冲突；</li>
 *   </ul>
 *   <li>业务执行完成后，把成功结果以相同的 key 写回缓存（TTL 10 分钟），并释放锁。</li>
 *   <li>后续相同 key 的请求：SET NX 失败且缓存里有结果 → 直接返回缓存结果（“重复提交”提示），
 *       不再执行第二次业务。</li>
 * </ol>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 幂等 key 前缀（用于区分不同业务，如 order_create、order_pay）。
     * 最终 Redis key = idempotent:{prefix}:{请求头 key}
     */
    String prefix() default "default";

    /**
     * 成功结果在 Redis 中的缓存时长（秒），默认 600 秒 = 10 分钟。
     */
    long expireSeconds() default 600L;
}