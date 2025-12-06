// DishService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;

public interface DishService {

    Result<Dish> getById(Long id);

    Page<Dish> getPage(Long restaurantId, Long categoryId, String keyword, int pageNum, int pageSize);

    void addDish(Dish dish);

    void updateDish(Dish dish);

    void toggleStatus(Long id);

    void deleteDish(Long id);
}
