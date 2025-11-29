// DishService.java
package com.jiumo.weicanjie.service;

import com.jiumo.weicanjie.common.Result;
import com.jiumo.weicanjie.entity.Dish;

public interface DishService {
    Result<Dish> getById(Long id);
}