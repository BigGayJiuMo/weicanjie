package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.service.RestaurantImageService;
import com.jiumo.weicanjie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/restaurant")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantImageService restaurantImageService;

    /**
     * 分页获取餐厅列表
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        return restaurantService.getPage(pageNum, pageSize, keyword);
    }

    /**
     * 创建餐厅
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Restaurant restaurant) {

        // categoryType 必须有默认值
        if (restaurant.getCategoryType() == null) {
            restaurant.setCategoryType(2); // 默认家常菜
        }

        restaurantService.save(restaurant);

        return Result.success(restaurant.getId()); //  返回新餐厅 ID
    }

    /**
     * 更新餐厅
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Restaurant restaurant) {
        boolean ok = restaurantService.updateById(restaurant);
        return ok ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除餐厅
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return restaurantService.deleteRestaurant(id);
    }


    /**
     * 切换营业状态（business_status）
     */
    @PostMapping("/toggle/{id}")
    public Result<?> toggleBusiness(@PathVariable Long id) {
        Restaurant r = restaurantService.getById(id);
        if (r == null) return Result.error("餐厅不存在");

        r.setBusinessStatus( (r.getBusinessStatus() != null && r.getBusinessStatus() == 1) ? 0 : 1);

        restaurantService.updateById(r);
        return Result.success("已切换状态");
    }

    @GetMapping("/image/list/{restaurantId}")
    public Result<?> listImages(@PathVariable Long restaurantId) {
        List<RestaurantImage> list = restaurantImageService.list(
                new QueryWrapper<RestaurantImage>().eq("restaurant_id", restaurantId)
        );
        return Result.success(list);
    }

    @PostMapping("/image/add")
    public Result<?> addImage(@RequestBody RestaurantImage img) {
        boolean ok = restaurantImageService.addImage(img);
        return ok ? Result.success("上传成功") : Result.error("上传失败");
    }

    @DeleteMapping("/image/delete/{id}")
    public Result<?> deleteImage(@PathVariable Long id) {
        boolean ok = restaurantImageService.removeById(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

}
