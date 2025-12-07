package com.jiumo.weicanjie.mapper;

import com.jiumo.weicanjie.entity.RefundRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RefundRecordMapper {

    @Insert("INSERT INTO refund_record (order_id, user_id, restaurant_id, refund_reason, refund_remark, status, previous_status) " +
            "VALUES (#{orderId}, #{userId}, #{restaurantId}, #{refundReason}, #{refundRemark}, #{status}, #{previousStatus})")
    int insertRefund(RefundRecord record);

    @Select("SELECT * FROM refund_record WHERE order_id = #{orderId} LIMIT 1")
    RefundRecord getByOrderId(Long orderId);

    @Update("UPDATE refund_record SET status = #{status}, approve_time = NOW() WHERE order_id = #{orderId}")
    int updateRefundStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

}
