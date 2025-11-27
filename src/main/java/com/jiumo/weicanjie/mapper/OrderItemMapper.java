package com.jiumo.weicanjie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiumo.weicanjie.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("SELECT oi.*, d.image_url as dish_image_url FROM order_items oi " +
            "LEFT JOIN dish d ON oi.dish_id = d.id " +
            "WHERE oi.order_id = #{orderId}")
    List<OrderItem> selectByOrderIdWithDishInfo(Long orderId);
}