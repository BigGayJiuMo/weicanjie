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

    /**
     * 分页查询餐厅列表
     * @param pageNum 页码，默认为1
     * @param pageSize 每页数据条数，默认为10
     * @param keyword 可选的搜索关键字
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 返回餐厅列表（根据角色权限不同返回不同数据）
     * @note
     *   - 超管可查看所有餐厅
     *   - 商家只能查看自己绑定的餐厅
     *   - 后厨无法查看餐厅管理信息
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

        // 商家只能查看自己的餐厅
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

        // 后厨角色不允许查看餐厅管理信息
        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限查看餐厅信息");
        }

        // 超级管理员可以正常分页查看所有餐厅
        return restaurantService.getPage(pageNum, pageSize, keyword);
    }

    /**
     * 创建餐厅
     * @param restaurant 餐厅信息
     * @param request HTTP 请求，用于获取用户角色
     * @return 创建结果
     * @note 仅超级管理员具有创建餐厅权限
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Restaurant restaurant, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        if (!"super".equals(role)) {
            return Result.error("只有超级管理员可以创建餐厅");
        }

        if (restaurant.getCategoryType() == null) {
            restaurant.setCategoryType(2); // 默认类别类型为2
        }

        restaurantService.save(restaurant);
        return Result.success(restaurant.getId()); // 返回创建的餐厅ID
    }

    /**
     * 更新餐厅信息
     * @param r 餐厅信息
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 更新结果
     * @note
     *   - 商家只能更新自己的餐厅信息
     *   - 后厨无法修改餐厅信息
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Restaurant r, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家只能修改自己绑定的餐厅
        if ("merchant".equals(role) && !r.getId().equals(rid)) {
            return Result.error("无权限修改别家餐厅");
        }

        // 后厨账号不能修改餐厅信息
        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限修改餐厅信息");
        }

        // 确保使用的是 status 字段，而不是 businessStatus
        return restaurantService.updateById(r)
                ? Result.success("更新成功")
                : Result.error("更新失败");
    }

    /**
     * 删除餐厅
     * @param id 餐厅ID
     * @param request HTTP 请求，用于获取用户角色
     * @return 删除结果
     * @note 仅超级管理员具有删除餐厅权限
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
     * 切换餐厅营业状态
     * @param id 餐厅ID
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 切换结果
     * @note
     *   - 商家只能切换自己餐厅的营业状态
     *   - 后厨无法修改餐厅营业状态
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

        // 修改 status 字段而不是 businessStatus
        r.setStatus((r.getStatus() != null && r.getStatus() == 1) ? 0 : 1);
        restaurantService.updateById(r);
        return Result.success("已切换状态");
    }


    /**
     * 获取餐厅图片列表
     * @param restaurantId 餐厅ID
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 图片列表
     * @note
     *   - 商家只能查看自己餐厅的图片
     *   - 后厨无法查看餐厅图片
     */
    @GetMapping("/image/list/{restaurantId}")
    public Result<?> listImages(@PathVariable Long restaurantId, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家只能查看自己餐厅的图片
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
     * 上传餐厅图片
     * @param img 图片实体
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 上传结果
     * @note
     *   - 商家只能上传自己餐厅的图片
     *   - 后厨无法上传图片
     */
    @PostMapping("/image/add")
    public Result<?> addImage(@RequestBody RestaurantImage img, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家只能上传自己餐厅的图片
        if ("merchant".equals(role) && !rid.equals(img.getRestaurantId())) {
            return Result.error("无权限上传图片到别家餐厅");
        }

        // 后厨无法上传餐厅图片
        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限上传图片");
        }

        boolean ok = restaurantImageService.addImage(img);
        return ok ? Result.success("上传成功") : Result.error("上传失败");
    }

    /**
     * 删除餐厅图片
     * @param id 图片ID
     * @param request HTTP 请求，用于获取用户角色和餐厅ID
     * @return 删除结果
     * @note
     *   - 商家只能删除自己餐厅的图片
     *   - 后厨无法删除图片
     */
    @DeleteMapping("/image/delete/{id}")
    public Result<?> deleteImage(@PathVariable Long id, HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        RestaurantImage img = restaurantImageService.getById(id);
        if (img == null) return Result.error("图片不存在");

        // 商家只能删除自己餐厅的图片
        if ("merchant".equals(role) && !rid.equals(img.getRestaurantId())) {
            return Result.error("无权限删除别家图片");
        }

        // 后厨无法删除餐厅图片
        if ("kitchen".equals(role)) {
            return Result.error("后厨账号无权限删除图片");
        }

        boolean ok = restaurantImageService.removeById(id);
        return ok ? Result.success("删除成功") : Result.error("删除失败");
    }

}
