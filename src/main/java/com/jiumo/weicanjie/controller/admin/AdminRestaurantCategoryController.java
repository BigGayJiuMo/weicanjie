package com.jiumo.weicanjie.controller.admin;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.RestaurantCategory;
import com.jiumo.weicanjie.service.RestaurantCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/restaurant-category")
public class AdminRestaurantCategoryController {

    @Autowired
    private RestaurantCategoryService categoryService;

    /** 查询所有餐厅分类 */
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(categoryService.list());
    }

    /** 新增餐厅分类 */
    @PostMapping("/add")
    public Result<?> add(@RequestBody RestaurantCategory c) {
        categoryService.save(c);
        return Result.success("创建成功");
    }

    /** 修改分类 */
    @PostMapping("/update")
    public Result<?> update(@RequestBody RestaurantCategory c) {
        categoryService.updateById(c);
        return Result.success("更新成功");
    }

    /** 删除分类 */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        categoryService.removeById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/sort")
    public Result<?> sort(@RequestBody List<RestaurantCategory> list) {
        // 按 id 更新 sortOrder
        categoryService.updateBatchById(list);
        return Result.success("排序已保存");
    }

}
