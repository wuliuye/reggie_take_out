package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:32
 * @description:菜品服务
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品的同时保存对应的口味的数据
     * @param dishDto
     * @return void
     **/
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表
        this.save(dishDto);
        //获取菜品id用于保存到菜品口味数据表
        Long dishId = dishDto.getId();

        //获取口味数据(无菜品id)
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        //将菜品id添加进口味数据
        dishFlavorList = dishFlavorList.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味数据表
        dishFlavorService.saveBatch(dishFlavorList);
    }
}
