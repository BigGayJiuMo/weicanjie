package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

/**
 * 餐厅服务接口，提供对餐厅相关操作的业务逻辑。
 * 包括获取餐厅列表、餐厅详情、餐厅分类、搜索餐厅等功能。
 */
public interface RestaurantService extends IService<Restaurant> {

    /**
     * 获取所有营业中的餐厅列表。
     *
     * @return 返回营业中的餐厅列表
     */
    Result<List<Restaurant>> getActiveRestaurants();

    /**
     * 获取指定餐厅的详细信息，包括营业时间、分类以及菜品信息。
     *
     * @param id 餐厅ID
     * @return 返回餐厅详情
     */
    Result<Restaurant> getRestaurantDetail(Long id);

    /**
     * 获取所有餐厅的列表。
     *
     * @return 返回所有餐厅的列表
     */
    Result<List<Restaurant>> getAllRestaurants();

    /**
     * 根据餐厅分类获取餐厅列表。
     *
     * @param categoryId 分类ID
     * @return 返回该分类下的餐厅列表
     */
    Result<List<Restaurant>> getByCategory(Integer categoryId);

    /**
     * 搜索餐厅，可以通过餐厅名称或描述进行模糊查询。
     *
     * @param keyword 搜索关键字
     * @return 返回符合搜索条件的餐厅列表
     */
    Result<List<Restaurant>> searchRestaurant(String keyword);

    /**
     * 提供实时联想功能，用于搜索提示。
     *
     * @param keyword 输入的搜索关键字
     * @return 返回联想的餐厅列表
     */
    Result<List<Restaurant>> suggest(String keyword);

    /**
     * 获取餐厅管理页面的数据，支持分页和按关键字搜索餐厅。
     *
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键字（可选）
     * @return 返回分页后的餐厅列表
     */
    Result<?> getPage(Integer pageNum, Integer pageSize, String keyword);

    Result<?> getPageByCategory(Integer categoryId, Integer pageNum, Integer pageSize);

    Result<?> getAdminPage(Integer pageNum, Integer pageSize, String keyword);
    /**
     * 删除指定的餐厅。
     *
     * @param id 餐厅ID
     * @return 删除结果
     */
    Result<?> deleteRestaurant(Long id);

    Result<Integer> getRestaurantStatus(Long restaurantId);

    void loadBusinessStatus(Restaurant restaurant);

}
