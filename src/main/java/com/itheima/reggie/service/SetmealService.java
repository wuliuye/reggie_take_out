package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:31
 * @description:
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐与菜品的关联
     * */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 套餐分页查询
     * */
    Page<SetmealDto> page(int page, int pageSize, String name);
}
