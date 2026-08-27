package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单项数据访问接口（Mapper），提供对 order_items 表的操作。
 * 包括根据订单ID查询订单项，并关联菜品信息的功能。
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID查询该订单的所有订单项，并联接菜品信息（如菜品图片）。
     *
     * @param orderId 订单ID
     * @return 返回该订单下的所有订单项，并包含每个订单项的菜品信息（如菜品图片URL）
     */
    @Select("SELECT oi.*, d.image_url as dish_image_url FROM order_items oi " +
            "LEFT JOIN dish d ON oi.dish_id = d.id " +
            "WHERE oi.order_id = #{orderId}")
    List<OrderItem> selectByOrderIdWithDishInfo(Long orderId);
}
