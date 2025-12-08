package com.jiumo.weicanjie.mapper;

import com.jiumo.weicanjie.entity.RefundRecord;
import org.apache.ibatis.annotations.*;

/**
 * 退款记录数据访问接口（Mapper），提供对 refund_record 表的操作。
 * 包括插入退款记录、查询退款记录和更新退款状态等功能。
 */
@Mapper
public interface RefundRecordMapper {

    /**
     * 插入一条退款记录。
     *
     * @param record 退款记录对象，包含订单ID、用户ID、餐厅ID、退款原因、退款备注等信息
     * @return 返回插入的记录数，0表示未插入任何记录
     */
    @Insert("INSERT INTO refund_record (order_id, user_id, restaurant_id, refund_reason, refund_remark, status, previous_status) " +
            "VALUES (#{orderId}, #{userId}, #{restaurantId}, #{refundReason}, #{refundRemark}, #{status}, #{previousStatus})")
    int insertRefund(RefundRecord record);

    /**
     * 根据订单ID查询退款记录。
     *
     * @param orderId 订单ID
     * @return 返回与指定订单ID匹配的退款记录（若存在）
     */
    @Select("SELECT * FROM refund_record WHERE order_id = #{orderId} LIMIT 1")
    RefundRecord getByOrderId(Long orderId);

    /**
     * 更新退款记录的状态。
     * 更新指定订单的退款状态，并记录审批时间。
     *
     * @param orderId 订单ID
     * @param status  退款状态：1-申请中，2-同意退款，3-拒绝退款
     * @return 返回更新的记录数，0表示未更新任何记录
     */
    @Update("UPDATE refund_record SET status = #{status}, approve_time = NOW() WHERE order_id = #{orderId}")
    int updateRefundStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}
