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

    /**
     * 获取菜品列表并进行分页查询
     * @param restaurantId 餐厅ID
     * @param categoryId 菜品分类ID（可选）
     * @param keyword 菜品名称关键词（可选）
     * @param pageNum 当前页码，默认为1
     * @param pageSize 每页记录数，默认为10
     * @param request HTTP请求对象，用于获取用户角色和餐厅ID
     * @return 分页查询结果，包含菜品信息
     * @throws UnauthorizedException 当用户无权限查询菜品时抛出异常
     */
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

        // 商家权限校验，确保商家只能访问自己的餐厅菜品
        if ("merchant".equals(role) && !restaurantId.equals(rid)) {
            return Result.error("无权限查看该餐厅菜品");
        }

        // 获取分页数据
        Page<Dish> page = dishService.getPage(restaurantId, categoryId, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 新增菜品
     * @param dish 新增的菜品对象
     * @param request HTTP请求对象，用于获取用户角色和餐厅ID
     * @return 操作结果，包含创建状态
     * @throws UnauthorizedException 当商家试图添加非自己餐厅的菜品时抛出异常
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Dish dish, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家权限校验，确保商家只能添加属于自己餐厅的菜品
        if ("merchant".equals(role)) {
            dish.setRestaurantId(rid);
        }

        // 保存新菜品
        dishService.addDish(dish);
        return Result.success("创建成功");
    }

    /**
     * 更新菜品信息
     * @param dish 要更新的菜品对象
     * @param request HTTP请求对象，用于获取用户角色和餐厅ID
     * @return 操作结果，包含更新状态
     * @throws UnauthorizedException 当商家试图修改非自己餐厅的菜品时抛出异常
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Dish dish, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家权限校验，确保商家只能修改自己餐厅的菜品
        if ("merchant".equals(role) && !dish.getRestaurantId().equals(rid)) {
            return Result.error("无权限修改该菜品");
        }

        // 更新菜品信息
        dishService.updateDish(dish);
        return Result.success("更新成功");
    }

    /**
     * 切换菜品的上下架状态
     * @param id 菜品ID
     * @return 操作结果，包含状态切换情况
     */
    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Long id) {
        // 切换菜品状态（上下架）
        dishService.toggleStatus(id);
        return Result.success("状态已切换");
    }

    /**
     * 删除指定菜品
     * @param id 菜品ID
     * @return 操作结果，包含删除状态
     * @throws InvalidOperationException 当菜品存在订单或相关依赖时，不能删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        // 删除菜品
        dishService.deleteDish(id);
        return Result.success("删除成功");
    }
}
