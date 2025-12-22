package com.jiumo.weicanjie.task;

import com.jiumo.weicanjie.mapper.OrderMapper;
import com.jiumo.weicanjie.mapper.RestaurantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RestaurantMonthlySalesTask {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

    /**
     * 每月 1 号 00:05 执行
     * 统计「上一个自然月」的餐厅月售
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    public void updateMonthlySales() {

        // 上一个自然月
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDate start = lastMonth.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = lastMonth.with(TemporalAdjusters.lastDayOfMonth());

        log.info("开始更新餐厅月售，统计区间：{} ~ {}", start, end);

        // 查询每个餐厅的月售
        List<Map<String, Object>> list =
                orderMapper.statRestaurantMonthlySales(start, end);

        for (Map<String, Object> row : list) {
            Long restaurantId = ((Number) row.get("restaurantId")).longValue();
            Integer sales = ((Number) row.get("monthlySales")).intValue();

            restaurantMapper.updateMonthlySales(restaurantId, sales);

            log.info("餐厅 {} 月售更新为 {}", restaurantId, sales);
        }

        log.info("餐厅月售更新完成");
    }
}
