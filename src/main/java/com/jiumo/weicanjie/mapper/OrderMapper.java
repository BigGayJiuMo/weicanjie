package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问接口（Mapper），提供对 orders 表的操作。
 * 包括查询订单、更新订单状态、处理支付信息、查询逾期未支付订单等功能。
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据用户ID查询该用户的所有订单，按创建时间降序排列。
     *
     * @param userId 用户ID
     * @return 返回该用户的所有订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Order> selectByUserId(Long userId);

    /**
     * 根据订单号查询订单信息。
     *
     * @param orderNumber 订单号
     * @return 返回与指定订单号匹配的订单信息（若存在）
     */
    @Select("SELECT * FROM orders WHERE order_number = #{orderNumber}")
    Order selectByOrderNumber(String orderNumber);

    /**
     * 更新订单状态，仅更新订单状态字段和更新时间。
     *
     * @param orderId 订单ID
     * @param status  新的订单状态
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE orders SET status = #{status}, updated_time = NOW() WHERE id = #{orderId}")
    int updateOrderStatusOnly(@Param("orderId") Long orderId, @Param("status") Integer status);

    /**
     * 更新订单支付状态，包括支付状态、支付时间、交易ID等。
     *
     * @param orderId      订单ID
     * @param status       订单状态
     * @param payStatus    支付状态
     * @param transactionId 交易ID
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE orders SET status = #{status}, pay_status = #{payStatus}, " +
            "transaction_id = #{transactionId}, pay_time = NOW(), updated_time = NOW() " +
            "WHERE id = #{orderId}")
    int updateOrderPaymentStatus(
            @Param("orderId") Long orderId,
            @Param("status") Integer status,
            @Param("payStatus") Integer payStatus,
            @Param("transactionId") String transactionId
    );

    /**
     * 查询逾期未支付的订单。
     *
     * @param expireTime 订单逾期时间（超过此时间未支付的订单）
     * @return 返回所有逾期未支付的订单列表
     */
    @Select("SELECT * FROM orders WHERE status = 1 AND created_time < #{expireTime}")
    List<Order> selectOverdueUnpaidOrders(LocalDateTime expireTime);

    /**
     * 搜索订单，根据用户ID和关键词，支持按餐厅名称或菜品名称搜索。
     *
     * @param userId  用户ID
     * @param keyword 搜索关键词（餐厅名称或菜品名称）
     * @return 返回符合条件的订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Map<String, Object>> searchOrders(
            @Param("userId") Long userId,
            @Param("keyword") String keyword
    );

    /**
     * 获取指定时间范围内的订单数据报告。
     * 该报告包含每个餐厅的订单数量、总销售额和销售的菜品数量。
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 返回订单数据报告
     */
    List<?> getReportData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
