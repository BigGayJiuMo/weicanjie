package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;
import com.jiumo.weicanjie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin/dish")
public class AdminDishController {

    @Autowired
    private DishService dishService;

    /** 分页查询菜品 */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家权限校验
        if ("merchant".equals(role) && !restaurantId.equals(rid)) {
            return Result.error("无权限查看该餐厅菜品");
        }

        Page<Dish> page = dishService.getPage(restaurantId, categoryId, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /** 新增菜品 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Dish dish, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role)) {
            dish.setRestaurantId(rid);
        }

        dishService.addDish(dish);
        return Result.success("创建成功");
    }

    /** 修改菜品 */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Dish dish, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role) && !dish.getRestaurantId().equals(rid)) {
            return Result.error("无权限修改该菜品");
        }

        dishService.updateDish(dish);
        return Result.success("更新成功");
    }

    /** 上架 / 下架 */
    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Long id) {
        dishService.toggleStatus(id);
        return Result.success("状态已切换");
    }

    /** 删除 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success("删除成功");
    }
}
