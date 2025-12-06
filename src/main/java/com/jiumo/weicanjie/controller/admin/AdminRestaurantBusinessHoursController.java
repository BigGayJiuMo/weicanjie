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

    @GetMapping("/list/{restaurantId}")
    public Result<?> list(@PathVariable Long restaurantId) {

        // 查询已有记录
        List<RestaurantBusinessHours> list = businessService.list(
                new QueryWrapper<RestaurantBusinessHours>()
                        .eq("restaurant_id", restaurantId)
                        .orderByAsc("day_of_week")
        );

        //  如果为空 → 初始化 7 天的营业模板
        if (list.isEmpty()) {
            for (int i = 1; i <= 7; i++) {
                RestaurantBusinessHours bh = new RestaurantBusinessHours();
                bh.setRestaurantId(restaurantId);
                bh.setDayOfWeek(i);
                bh.setIsOpen(0);
                bh.setOpenTime(java.time.LocalTime.of(9, 0));
                bh.setCloseTime(java.time.LocalTime.of(21, 0));

                list.add(bh);
            }
        }

        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody RestaurantBusinessHours bh) {
        businessService.save(bh);
        return Result.success("已新增");
    }


    @PostMapping("/update")
    public Result<?> update(@RequestBody RestaurantBusinessHours bh, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        Long rid = (Long) request.getAttribute("restaurantId");

        // 商家只能改自己的餐厅
        if ("merchant".equals(role) && !rid.equals(bh.getRestaurantId())) {
            return Result.error("无权限修改别家餐厅营业时间");
        }

        businessService.updateById(bh);
        return Result.success("已更新");
    }

}