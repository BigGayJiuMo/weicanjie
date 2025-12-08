package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Order> selectByUserId(Long userId);

    @Select("SELECT * FROM orders WHERE order_number = #{orderNumber}")
    Order selectByOrderNumber(String orderNumber);

    @Update("UPDATE orders SET status = #{status}, updated_time = NOW() WHERE id = #{orderId}")
    int updateOrderStatusOnly(@Param("orderId") Long orderId, @Param("status") Integer status);

    @Update("UPDATE orders SET status = #{status}, pay_status = #{payStatus}, " +
            "transaction_id = #{transactionId}, pay_time = NOW(), updated_time = NOW() " +
            "WHERE id = #{orderId}")
    int updateOrderPaymentStatus(
            @Param("orderId") Long orderId,
            @Param("status") Integer status,
            @Param("payStatus") Integer payStatus,
            @Param("transactionId") String transactionId
    );

    @Select("SELECT * FROM orders WHERE status = 1 AND created_time < #{expireTime}")
    List<Order> selectOverdueUnpaidOrders(LocalDateTime expireTime);


    /** 搜索订单（XML 或注解均可） */
    List<Map<String, Object>> searchOrders(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    List<?> getReportData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> getSalesTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("groupBy") String groupBy
    );
}
