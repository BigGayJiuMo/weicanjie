package com.jiumo.weicanjie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.mapper.RestaurantImageMapper;
import com.jiumo.weicanjie.service.RestaurantImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 餐厅图片服务实现类。
 * 该类提供餐厅图片相关的操作，包括添加餐厅图片。
 */
@Service
public class RestaurantImageServiceImpl
        extends ServiceImpl<RestaurantImageMapper, RestaurantImage>
        implements RestaurantImageService {

    @Autowired
    private RestaurantImageMapper restaurantImageMapper;

    /**
     * 添加餐厅图片。
     *
     * @param img 餐厅图片对象，包含餐厅ID和图片URL等信息
     * @return 如果插入成功则返回 true，否则返回 false
     * @throws RuntimeException 如果餐厅图片超过三张则抛出异常
     * @note 该方法会检查每个餐厅的图片数量，最多允许上传 3 张图片。
     */
    @Override
    public boolean addImage(RestaurantImage img) {

        // 查询餐厅已有的图片数量
        long count = restaurantImageMapper.selectCount(
                new QueryWrapper<RestaurantImage>().eq("restaurant_id", img.getRestaurantId())
        );

        // 如果已上传的图片数量大于等于 3 张，则抛出异常
        if (count >= 3) {
            throw new RuntimeException("单个餐厅最多只能上传 3 张图片");
        }

        // 执行插入操作，返回插入结果
        return restaurantImageMapper.insert(img) > 0;
    }
}
