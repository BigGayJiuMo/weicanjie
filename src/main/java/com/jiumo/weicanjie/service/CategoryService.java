// CategoryService.java
package com.jiumo.weicanjie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiumo.weicanjie.entity.Category;
import com.jiumo.weicanjie.common.Result;
import java.util.List;

public interface CategoryService extends IService<Category> {

    Result<List<Category>> getCategoriesByRestaurant(Long restaurantId);

    Result<List<Category>> getCategoriesWithDishes(Long restaurantId);
}