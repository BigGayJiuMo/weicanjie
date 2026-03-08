package com.jiumo.weicanjie.controller;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.service.RestaurantImageService;
import com.jiumo.weicanjie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐厅管理控制器
 * 该控制器提供餐厅的增删改查（CRUD）操作，包括获取餐厅的基本信息、分类、图片以及实时搜索等功能。
 */
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantImageService restaurantImageService;

    /**
     * 获取所有营业中的餐厅
     *
     * 该接口返回所有处于营业状态的餐厅列表。
     *
     * @return 返回营业中的餐厅列表
     */
    @GetMapping("/active")
    public Result<List<Restaurant>> getActiveRestaurants() {
        Result<List<Restaurant>> result = restaurantService.getActiveRestaurants();

        if (result.getCode() == 200) {
            result.getData().forEach(restaurantService::loadBusinessStatus);
        }
        return result;
    }

    /**
     * 获取所有餐厅
     *
     * 该接口返回所有餐厅的完整列表。
     *
     * @return 返回所有餐厅列表
     */
    @GetMapping("/all")
    public Result<List<Restaurant>> getAllRestaurants() {
        Result<List<Restaurant>> result = restaurantService.getAllRestaurants();

        if (result.getCode() == 200) {
            result.getData().forEach(restaurantService::loadBusinessStatus);
        }
        return result;
    }

    /**
     * 分页获取餐厅列表（支持关键字搜索）
     *
     * @param pageNum  当前页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @param keyword  搜索关键字（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<?> getRestaurantPage(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return restaurantService.getPage(pageNum, pageSize, keyword);
    }

    /**
     * 获取餐厅详情（包括分类和菜品）
     *
     * 该接口用于获取指定餐厅的详细信息，包括餐厅的分类和菜品等信息。
     *
     * @param id 餐厅ID
     * @return 返回餐厅的详细信息
     */
    @GetMapping("/{id}")
    public Result<Restaurant> getRestaurantDetail(@PathVariable Long id) {
        return restaurantService.getRestaurantDetail(id);
    }

    /**
     * 获取餐厅的基本信息
     *
     * 该接口用于获取指定餐厅的基本信息。
     *
     * @param id 餐厅ID
     * @return 返回餐厅的基本信息，如果餐厅不存在，返回错误信息
     */
    @GetMapping("/basic/{id}")
    public Result<Restaurant> getRestaurantBasic(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        if (restaurant != null) {
            restaurantService.loadBusinessStatus(restaurant);
            return Result.success(restaurant);
        } else {
            return Result.error("餐厅不存在");
        }
    }


    /**
     * 获取餐厅分类信息
     *
     * 该接口根据餐厅的分类ID获取对应的餐厅列表。
     *
     * @param categoryId 分类ID
     * @return 返回指定分类的餐厅列表
     */
    @GetMapping("/listByCategory")
    public Result<List<Restaurant>> listByCategory(@RequestParam Integer categoryId) {
        Result<List<Restaurant>> result = restaurantService.getByCategory(categoryId);

        if (result.getCode() == 200) {
            result.getData().forEach(restaurantService::loadBusinessStatus);
        }
        return result;
    }

    /**
     * 添加餐厅图片
     *
     * 该接口用于向指定餐厅添加图片。
     *
     * @param img 餐厅图片对象，包含图片的相关信息
     * @return 返回添加图片的结果，成功或失败
     */
    @PostMapping("/image/add")
    public Result<String> addRestaurantImage(@RequestBody RestaurantImage img) {
        try {
            boolean ok = restaurantImageService.addImage(img);
            return ok ? Result.success("添加成功") : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 搜索餐厅（按名称或描述）
     *
     * 该接口允许用户根据餐厅名称或描述进行模糊搜索。
     *
     * @param keyword 搜索关键词，可以是餐厅名称或描述
     * @return 返回匹配搜索条件的餐厅列表
     */
    @GetMapping("/search")
    public Result<List<Restaurant>> searchRestaurant(@RequestParam String keyword) {
        Result<List<Restaurant>> result = restaurantService.searchRestaurant(keyword);

        if (result.getCode() == 200) {
            result.getData().forEach(restaurantService::loadBusinessStatus);
        }
        return result;
    }


    /**
     * 实时联想搜索（只返回餐厅ID和名称）
     *
     * 该接口用于根据关键词实时联想餐厅，仅返回餐厅的ID和名称，适用于搜索框实时提示功能。
     *
     * @param keyword 搜索关键词
     * @return 返回匹配关键词的餐厅ID和名称列表
     */
    @GetMapping("/suggest")
    public Result<List<Restaurant>> suggest(@RequestParam String keyword) {
        Result<List<Restaurant>> result = restaurantService.suggest(keyword);

        if (result.getCode() == 200) {
            result.getData().forEach(restaurantService::loadBusinessStatus);
        }
        return result;
    }

    /**
     * 获取餐厅状态
     *
     * @param id 餐厅ID
     * @return 返回餐厅状态
     */
    @GetMapping("/status/{id}")
    public Result<Integer> getRestaurantStatus(@PathVariable Long id) {
        return restaurantService.getRestaurantStatus(id);
    }

}
