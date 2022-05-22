package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:31
 * @description:
 */
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品的同时保存对应的口味的数据
     * */
    void saveWithFlavor(DishDto dishDto);
}
