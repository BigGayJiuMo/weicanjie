package com.jiumo.weicanjie.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.RestaurantBusinessHours;
import com.jiumo.weicanjie.service.RestaurantBusinessHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/admin/restaurant/business")
public class AdminRestaurantBusinessHoursController {

    @Autowired
    private RestaurantBusinessHoursService businessService;

    /**
     * 获取餐厅的营业时间列表
     * @param restaurantId 餐厅ID
     * @return 返回餐厅的营业时间列表。如果未设置，返回默认的7天营业时间模板
     * @note 如果餐厅没有营业时间记录，将会初始化7天的营业时间模板，默认从上午9点到晚上9点
     */
    @GetMapping("/list/{restaurantId}")
    public Result<?> list(@PathVariable Long restaurantId) {

        // 查询餐厅已有的营业时间记录
        List<RestaurantBusinessHours> list = businessService.list(
                new QueryWrapper<RestaurantBusinessHours>()
                        .eq("restaurant_id", restaurantId)
                        .orderByAsc("day_of_week") // 按周几排序
        );

        // 如果餐厅没有营业时间记录，初始化7天的营业时间模板
        if (list.isEmpty()) {
            for (int i = 1; i <= 7; i++) {
                RestaurantBusinessHours bh = new RestaurantBusinessHours();
                bh.setRestaurantId(restaurantId);
                bh.setDayOfWeek(i);
                bh.setIsOpen(0); // 默认不开门
                bh.setOpenTime(java.time.LocalTime.of(9, 0)); // 默认开门时间 09:00
                bh.setCloseTime(java.time.LocalTime.of(21, 0)); // 默认关门时间 21:00

                list.add(bh); // 添加到列表中
            }
        }

        return Result.success(list); // 返回餐厅的营业时间列表
    }

    /**
     * 新增餐厅营业时间
     * @param bh 餐厅营业时间实体
     * @return 返回操作结果
     * @note 管理员或商家都可以新增营业时间
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody RestaurantBusinessHours bh) {
        businessService.save(bh); // 保存营业时间
        return Result.success("已新增"); // 返回成功提示
    }

    /**
     * 更新餐厅营业时间
     * @param bh 餐厅营业时间实体
     * @param request HttpServletRequest 用于获取当前请求中的用户角色和餐厅ID
     * @return 返回操作结果
     * @note 商家只能更新自己餐厅的营业时间
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody RestaurantBusinessHours bh, HttpServletRequest request) {
        String role = (String) request.getAttribute("role"); // 获取当前用户角色
        Long rid = (Long) request.getAttribute("restaurantId"); // 获取当前餐厅ID

        // 商家权限校验：商家只能修改自己餐厅的营业时间
        if ("merchant".equals(role) && !rid.equals(bh.getRestaurantId())) {
            return Result.error("无权限修改别家餐厅营业时间"); // 权限不足，返回错误
        }

        businessService.updateById(bh); // 更新营业时间
        return Result.success("已更新"); // 返回成功提示
    }

}
