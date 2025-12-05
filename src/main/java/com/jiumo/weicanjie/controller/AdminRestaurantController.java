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
            @RequestParam long pageNum,
            @RequestParam long pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Restaurant> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Restaurant> qw = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("name", keyword).or().like("description", keyword);
        }

        qw.orderByDesc("id");

        return Result.success(restaurantService.page(page, qw));
    }

    /**
     * 创建餐厅
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Restaurant restaurant) {
        restaurant.setId(null);
        boolean ok = restaurantService.save(restaurant);
        return ok ? Result.success("创建成功") : Result.error("创建失败");
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
        boolean ok = restaurantService.removeById(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
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

    /**
     * 为餐厅添加轮播图
     */
    @PostMapping("/image/add")
    public Result<?> addImage(@RequestBody RestaurantImage img) {

        if (img.getRestaurantId() == null) {
            return Result.error("restaurantId 不能为空");
        }

        boolean ok = restaurantImageService.addImage(img);
        return ok ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 获取餐厅轮播图
     */
    @GetMapping("/image/list/{restaurantId}")
    public Result<?> listImages(@PathVariable Long restaurantId) {
        return Result.success(restaurantImageService.lambdaQuery()
                .eq(RestaurantImage::getRestaurantId, restaurantId)
                .orderByAsc(RestaurantImage::getSortOrder)
                .list());
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/image/delete/{id}")
    public Result<?> deleteImage(@PathVariable Long id) {
        return restaurantImageService.removeById(id)
                ? Result.success("删除成功") : Result.error("删除失败");
    }
}
