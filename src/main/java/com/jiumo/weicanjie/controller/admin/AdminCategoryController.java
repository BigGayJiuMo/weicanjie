package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.DishCategory;
import com.jiumo.weicanjie.service.DishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private DishCategoryService dishCategoryService;

    /**
     * 分页查询餐厅分类信息
     * @param restaurantId 餐厅ID
     * @param pageNum 当前页码，默认为1
     * @param pageSize 每页记录数，默认为10
     * @param keyword 搜索关键字（可选）
     * @param request HttpServletRequest，用于获取当前用户角色和餐厅ID
     * @return 包含分类信息的分页结果
     * @throws UnauthorizedException 当用户权限不足时抛出异常
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam Long restaurantId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家角色权限校验，只能查询自己的餐厅分类
        if ("merchant".equals(role) && !restaurantId.equals(rid)) {
            return Result.error("无权限查看该餐厅分类");
        }

        // 创建分页对象
        Page<DishCategory> page = new Page<>(pageNum, pageSize);

        // 查询餐厅分类，支持根据名称进行模糊搜索
        dishCategoryService.lambdaQuery()
                .eq(DishCategory::getRestaurantId, restaurantId)
                .like(keyword != null && !keyword.isEmpty(), DishCategory::getName, keyword)
                .orderByAsc(DishCategory::getSortOrder)
                .page(page);

        return Result.success(page);
    }

    /**
     * 新增餐厅菜品分类
     * @param category 餐厅菜品分类对象
     * @param request HttpServletRequest，用于获取当前用户角色和餐厅ID
     * @return 操作结果
     * @throws UnauthorizedException 当商家尝试创建非自己餐厅的分类时抛出异常
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody DishCategory category, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家角色自动绑定餐厅ID
        if ("merchant".equals(role)) {
            category.setRestaurantId(rid);
        }

        // 保存菜品分类
        dishCategoryService.save(category);
        return Result.success("创建成功");
    }

    /**
     * 修改餐厅菜品分类
     * @param category 更新后的餐厅菜品分类对象
     * @param request HttpServletRequest，用于获取当前用户角色和餐厅ID
     * @return 操作结果
     * @throws UnauthorizedException 当商家尝试修改非自己餐厅的分类时抛出异常
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody DishCategory category, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家角色权限校验，只能修改自己餐厅的分类
        if ("merchant".equals(role) && !category.getRestaurantId().equals(rid)) {
            return Result.error("无权限修改该分类");
        }

        // 更新菜品分类
        dishCategoryService.updateById(category);
        return Result.success("更新成功");
    }

    /**
     * 删除餐厅菜品分类
     * @param id 菜品分类ID
     * @return 操作结果
     * @throws IllegalStateException 当分类下存在菜品时无法删除
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {

        // 检查该分类下是否有菜品
        if (dishCategoryService.hasDish(id)) {
            return Result.error("分类下存在菜品，无法删除");
        }

        // 删除菜品分类
        dishCategoryService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 切换餐厅菜品分类的状态（启用/禁用）
     * @param id 菜品分类ID
     * @return 操作结果
     * @throws NotFoundException 当分类不存在时抛出异常
     */
    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Long id) {

        // 获取菜品分类对象
        DishCategory c = dishCategoryService.getById(id);
        if (c == null) return Result.error("分类不存在");

        // 切换分类状态：启用/禁用
        c.setStatus(c.getStatus() == 1 ? 0 : 1);
        dishCategoryService.updateById(c);

        return Result.success("状态已切换");
    }

    /**
     * 批量更新餐厅菜品分类的排序
     * @param list 菜品分类的排序列表
     * @return 操作结果
     */
    @PostMapping("/sort")
    public Result<?> sort(@RequestBody List<DishCategory> list) {

        // 批量更新分类排序
        dishCategoryService.updateBatchById(list);

        return Result.success("排序已保存");
    }
}
