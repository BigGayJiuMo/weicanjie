package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.RestaurantImage;

/**
 * 餐厅图片服务接口，提供餐厅图片相关的操作。
 * 包括添加餐厅图片等操作。
 */
public interface RestaurantImageService extends IService<RestaurantImage> {

    /**
     * 添加餐厅图片
     *
     * @param img 餐厅图片对象
     * @return 如果添加成功则返回 true，否则返回 false
     * @note 该方法会将图片保存到数据库，确保图片的关联性（餐厅ID等）
     */
    boolean addImage(RestaurantImage img);

}
