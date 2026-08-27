package com.jiumo.weicanjie.task;

import com.jiumo.weicanjie.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CartCleanTask {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 每天凌晨3点清理已停业餐厅的购物车记录
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanClosedRestaurantCarts() {
        try {
            int deletedCount = cartMapper.deleteClosedRestaurantCarts();
            if (deletedCount > 0) {
                System.out.println("清理已停业餐厅购物车记录，共清理：" + deletedCount + "条");
            }
        } catch (Exception e) {
            System.err.println("清理购物车任务失败：" + e.getMessage());
        }
    }
}