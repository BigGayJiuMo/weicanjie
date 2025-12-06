// AdminRestaurantController.java
package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Restaurant;
import com.jiumo.weicanjie.entity.RestaurantImage;
import com.jiumo.weicanjie.service.RestaurantImageService;
import com.jiumo.weicanjie.service.RestaurantService;
import com.jiumo.weicanjie.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin/restaurant")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantImageService restaurantImageService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 分页获取餐厅（
     *  super：看到全部
     *  merchant：只看到自己绑定的那一家
     *  kitchen：不能看餐厅管理
     */
    @GetMapping("/page")
    public Result<?> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // ⭐ 商家账号：只返回自己餐厅，封装成 Page 结构，前端不用改
        if ("merchant".equals(role)) {
            Restaurant r = restaurantService.getById(rid);

            Page<Restaurant> page = new Page<>(pageNum, pageSize);
            if (r != null) {
                page.setRecords(Collections.singletonList(r));
                page.setTotal(1);
            } else {
                page.setRecords(Collections.emptyList());
                page.setTotal(0);
            }
            return Result.success(page);
        }

        // ⭐ 后厨账号：不允许访问餐厅管理
        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限查看餐厅信息");
        }

        // ⭐ 超管：正常分页
        return restaurantService.getPage(pageNum, pageSize, keyword);
    }

    /**
     * 创建餐厅（只有 super）
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Restaurant restaurant, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        if (!"super".equals(role)) {
            return Result.error("只有超级管理员可以创建餐厅");
        }

        if (restaurant.getCategoryType() == null) {
            restaurant.setCategoryType(2);
        }

        restaurantService.save(restaurant);
        return Result.success(restaurant.getId());
    }

    /**
     * 更新餐厅（merchant 只能修改自己的；kitchen 不允许）
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Restaurant r, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role)) {
            if (!r.getId().equals(rid)) {
                return Result.error("无权限修改别家餐厅");
            }
        }

        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限修改餐厅信息");
        }

        return restaurantService.updateById(r)
                ? Result.success("更新成功")
                : Result.error("更新失败");
    }

    /**
     * 删除餐厅（只有 super）
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        if (!"super".equals(role)) {
            return Result.error("只有超级管理员可以删除餐厅");
        }

        return restaurantService.deleteRestaurant(id);
    }

    /**
     * 切换营业状态（merchant 只能改自己；kitchen 不允许）
     */
    @PostMapping("/toggle/{id}")
    public Result<?> toggleBusiness(@PathVariable Long id, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role) && !rid.equals(id)) {
            return Result.error("不能操作别家餐厅营业状态");
        }

        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无法修改营业状态");
        }

        Restaurant r = restaurantService.getById(id);
        if (r == null) return Result.error("餐厅不存在");

        r.setBusinessStatus((r.getBusinessStatus() != null && r.getBusinessStatus() == 1) ? 0 : 1);

        restaurantService.updateById(r);
        return Result.success("已切换状态");
    }

    /**
     * 图片列表（merchant 只能看自己的）
     */
    @GetMapping("/image/list/{restaurantId}")
    public Result<?> listImages(@PathVariable Long restaurantId, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role) && !rid.equals(restaurantId)) {
            return Result.error("无权限查看别家餐厅图片");
        }

        List<RestaurantImage> list = restaurantImageService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RestaurantImage>()
                        .eq("restaurant_id", restaurantId)
        );
        return Result.success(list);
    }

    /**
     * 上传图片（merchant 只能给自己店上传；kitchen 不允许）
     */
    @PostMapping("/image/add")
    public Result<?> addImage(@RequestBody RestaurantImage img, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        if ("merchant".equals(role) && !rid.equals(img.getRestaurantId())) {
            return Result.error("无权限上传图片到别家餐厅");
        }

        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限上传图片");
        }

        boolean ok = restaurantImageService.addImage(img);
        return ok ? Result.success("上传成功") : Result.error("上传失败");
    }

    /**
     * 删除图片（merchant 只能删自己的；kitchen 不允许）
     */
    @DeleteMapping("/image/delete/{id}")
    public Result<?> deleteImage(@PathVariable Long id, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        RestaurantImage img = restaurantImageService.getById(id);
        if (img == null) return Result.error("图片不存在");

        if ("merchant".equals(role) && !rid.equals(img.getRestaurantId())) {
            return Result.error("无权限删除别家图片");
        }

        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限删除图片");
        }

        boolean ok = restaurantImageService.removeById(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

}
