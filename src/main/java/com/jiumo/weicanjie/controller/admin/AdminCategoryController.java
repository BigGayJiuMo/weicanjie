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

    /** 分页查询分类（super 可查询任意；merchant 只能查自己的） */
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

        // 商家权限限制
        if ("merchant".equals(role) && !restaurantId.equals(rid)) {
            return Result.error("无权限查看该餐厅分类");
        }

        Page<DishCategory> page = new Page<>(pageNum, pageSize);

        dishCategoryService.lambdaQuery()
                .eq(DishCategory::getRestaurantId, restaurantId)
                .like(keyword != null && !keyword.isEmpty(), DishCategory::getName, keyword)
                .orderByAsc(DishCategory::getSortOrder)
                .page(page);

        return Result.success(page);
    }

    /** 新增分类（merchant 自动绑定餐厅） */
    @PostMapping("/add")
    public Result<?> add(@RequestBody DishCategory category, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role)) {
            category.setRestaurantId(rid);
        }

        dishCategoryService.save(category);
        return Result.success("创建成功");
    }

    /** 修改分类 */
    @PostMapping("/update")
    public Result<?> update(@RequestBody DishCategory category, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role) && !category.getRestaurantId().equals(rid)) {
            return Result.error("无权限修改该分类");
        }

        dishCategoryService.updateById(category);
        return Result.success("更新成功");
    }

    /** 删除分类（禁止删除有菜品的分类） */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {

        if (dishCategoryService.hasDish(id)) {
            return Result.error("分类下存在菜品，无法删除");
        }

        dishCategoryService.removeById(id);
        return Result.success("删除成功");
    }

    /** 切换状态：启用 / 禁用 */
    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Long id) {

        DishCategory c = dishCategoryService.getById(id);
        if (c == null) return Result.error("分类不存在");

        c.setStatus(c.getStatus() == 1 ? 0 : 1);
        dishCategoryService.updateById(c);

        return Result.success("状态已切换");
    }

    /** 批量排序 */
    @PostMapping("/sort")
    public Result<?> sort(@RequestBody List<DishCategory> list) {

        dishCategoryService.updateBatchById(list);

        return Result.success("排序已保存");
    }
}
