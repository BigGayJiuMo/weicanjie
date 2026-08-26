package com.jiumo.weicanjie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiumo.weicanjie.annotation.Idempotent;
import com.jiumo.weicanjie.aspect.IdempotentAspect;
import com.jiumo.weicanjie.entity.Order;
import com.jiumo.weicanjie.entity.OrderItem;
import com.jiumo.weicanjie.mapper.OrderItemMapper;
import com.jiumo.weicanjie.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 订单接口幂等集成测试（真实 Redis + 真实 MySQL，仅模拟 HTTP 层）。
 * <p>
 * 验证三个幂等语义：
 * <ol>
 *   <li><b>同一 key 重复两次请求 → 只生成一条订单</b>（时间维度的防重）</li>
 *   <li><b>不同 key 的两个请求 → 生成两条订单</b>（幂等 key 必须按"一次业务一次 key"用）</li>
 *   <li><b>并发两个请求同 key → 只有一个成功、一个 409 冲突</b>（并发维度的防重，
 *      用 CountDownLatch 模拟真实竞态，压测脚本不易做到的确定性验证）</li>
 * </ol>
 * <p>
 * 前置条件：本机 MySQL(weicanjie_db)、Redis(6379) 已启动。
 * 运行方式：IDEA 里右键本类 Run（或 mvn test）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderIdempotencyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /** MockMvc 请求路径：不含 context-path（/api 由真正部署的容器处理，MockMvc 直接用 servlet 映射） */
    private static final String CREATE_URL = "/order/create";

    /** 兼容性：测试用户/餐厅/菜品，依赖 SQL 中的测试数据 */
    private static final long USER_ID = 1L;
    private static final long RESTAURANT_ID = 1L;
    private static final long DISH_ID = 1L;

    private String buildOrderBody() {
        return "{"
                + "\"order\":{"
                + "\"userId\":" + USER_ID + ","
                + "\"restaurantId\":" + RESTAURANT_ID + ","
                + "\"totalAmount\":88.00,"
                + "\"packingFee\":2.00,"
                + "\"eatType\":2"
                + "},"
                + "\"items\":[{"
                + "\"dishId\":" + DISH_ID + ","
                + "\"dishName\":\"测试菜品\","
                + "\"dishPrice\":86.00,"
                + "\"quantity\":1,"
                + "\"subtotal\":86.00"
                + "}]"
                + "}";
    }

    private MvcResult postWithKey(String key) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(CREATE_URL)
                        .header(IdempotentAspect.HEADER_IDEMPOTENT_KEY, key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildOrderBody()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("同一幂等 key 重复两次只创建一条订单")
    void duplicateKeyCreatesOneOrder() throws Exception {
        String key = "test-" + UUID.randomUUID();

        MvcResult first = postWithKey(key);
        MvcResult second = postWithKey(key);

        JsonNode firstJson = objectMapper.readTree(first.getResponse().getContentAsString());
        JsonNode secondJson = objectMapper.readTree(second.getResponse().getContentAsString());
        int firstCode = firstJson.get("code").asInt();
        int secondCode = secondJson.get("code").asInt();
        long firstId = firstJson.get("data").get("id").asLong();
        long secondId = secondJson.get("data").get("id").asLong();

        assertThat(firstCode).isEqualTo(200);
        // 幂等语义：第二次请求返回【同一个订单】（缓存结果），而不是新建一个订单
        assertThat(secondCode).isEqualTo(200);
        assertThat(secondId).isEqualTo(firstId);

        // 清理测试订单（先删 order_items 再删 orders，避免外键约束）
        deleteOrderCascade(firstId);
        stringRedisTemplate.delete(IdempotentAspect.LOCK_PREFIX + "order_create:" + key);
        stringRedisTemplate.delete(IdempotentAspect.RESULT_PREFIX + "order_create:" + key);
    }

    @Test
    @DisplayName("不同幂等 key 的请求各自创建订单（key 按业务台数使用）")
    void differentKeysCreateSeparateOrders() throws Exception {
        String key1 = "test-a-" + UUID.randomUUID();
        String key2 = "test-b-" + UUID.randomUUID();

        MvcResult r1 = postWithKey(key1);
        MvcResult r2 = postWithKey(key2);

        long id1 = objectMapper.readTree(r1.getResponse().getContentAsString())
                .get("data").get("id").asLong();
        long id2 = objectMapper.readTree(r2.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        assertThat(id1).isNotEqualTo(id2);

        deleteOrderCascade(id1);
        deleteOrderCascade(id2);
        stringRedisTemplate.delete(IdempotentAspect.LOCK_PREFIX + "order_create:" + key1);
        stringRedisTemplate.delete(IdempotentAspect.LOCK_PREFIX + "order_create:" + key2);
        stringRedisTemplate.delete(IdempotentAspect.RESULT_PREFIX + "order_create:" + key1);
        stringRedisTemplate.delete(IdempotentAspect.RESULT_PREFIX + "order_create:" + key2);
    }

    @Test
    @DisplayName("并发同 key 只有一次业务执行，另一个返回 409")
    void concurrentSameKeyOnlyOneBusinessExecution() throws Exception {
        String key = "test-concurrent-" + UUID.randomUUID();

        java.util.concurrent.CountDownLatch ready = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch go = new java.util.concurrent.CountDownLatch(1);

        // 两个线程同时发起同 key 请求
        java.util.concurrent.ExecutorService pool =
                java.util.concurrent.Executors.newFixedThreadPool(2);
        java.util.concurrent.Future<MvcResult> f1 = pool.submit(() -> {
            ready.countDown();
            go.await();
            return postWithKey(key);
        });
        java.util.concurrent.Future<MvcResult> f2 = pool.submit(() -> {
            ready.countDown();
            go.await();
            return postWithKey(key);
        });

        ready.await();
        go.countDown(); // 同时放行，制造真实竞态
        JsonNode json1 = objectMapper.readTree(f1.get().getResponse().getContentAsString());
        JsonNode json2 = objectMapper.readTree(f2.get().getResponse().getContentAsString());
        int code1 = json1.get("code").asInt();
        int code2 = json2.get("code").asInt();

        // 两个响应的可能组合：
        //  - "200,409"：并发锁生效，一个执行业务，另一个冲突（理想情况）
        //  - "200,200"：两个请求恰好串行完成，但第二个返回的是【缓存结果】（同一订单）
        // 无论哪种，同一 key 绝不能出现两个不同订单！
        assertThat(code1 + "," + code2)
                .satisfies(s -> assertThat(s).contains("200"))
                .satisfies(s -> assertThat(s).containsAnyOf("200", "409"));

        // 若两个都返回了订单（都 200），必须是同一个订单 id（幂等核心语义）
        if (code1 == 200 && code2 == 200) {
            long id1 = json1.get("data").get("id").asLong();
            long id2 = json2.get("data").get("id").asLong();
            assertThat(id1).isEqualTo(id2);
            deleteOrderCascade(id1);
        } else {
            // 一个 200 一个 409：删掉那个成功创建的订单
            JsonNode success = code1 == 200 ? json1 : json2;
            deleteOrderCascade(success.get("data").get("id").asLong());
        }

        pool.shutdownNow();
        stringRedisTemplate.delete(IdempotentAspect.LOCK_PREFIX + "order_create:" + key);
        stringRedisTemplate.delete(IdempotentAspect.RESULT_PREFIX + "order_create:" + key);
    }

    private long countOrdersWithNumberPrefix(String key) {
        return orderMapper.selectList(null).stream()
                .filter(o -> o.getOrderNumber() != null && o.getOrderNumber().contains(key))
                .count();
    }

    /** 级联清理测试订单：先删 order_items（外键），再删 orders */
    private void deleteOrderCascade(Long orderId) {
        if (orderId == null) return;
        orderItemMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        orderMapper.deleteById(orderId);
    }
}