package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.transaction.annotation.Transactional;

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
     *
     * @param dishDto
     * @return void
     **/
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表
        this.save(dishDto);
        //获取菜品id用于保存到菜品口味数据表
        Long dishId = dishDto.getId();

        //前台传递过来的口味数据只有name和value属性，所以需要设置口味数据对应的菜品id
        //获取前台传递过来的口味数据(无菜品id)
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        //将菜品id添加进口味数据
        dishFlavorList = dishFlavorList.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味数据表
        dishFlavorService.saveBatch(dishFlavorList);
    }

    /**
     * 根据id查询菜品信息，包括口味数据
     *
     * @param id
     * @return com.itheima.reggie.dto.DishDto
     **/
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品基本信息
        Dish dish = this.getById(id);
        //根据菜品Id查询菜品有哪些口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品的同时修改对应的口味的数据
     *
     * @param dishDto
     * @return void
     **/
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //1.更新菜品基本信息
        this.updateById(dishDto);

        //2.删除该菜品对应的菜品口味数据
        Long dishDtoId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dishDtoId);
        dishFlavorService.remove(queryWrapper);

        //前台传递过来的口味数据只有name和value属性，所以需要设置口味数据对应的菜品id
        //获取前台传递过来的口味数据(无菜品id)
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        //将菜品id添加进口味数据
        dishFlavorList = dishFlavorList.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishDtoId);
            return dishFlavor;
        }).collect(Collectors.toList());

        //3.保存传过来的口味菜品数据
        dishFlavorService.saveBatch(dishFlavorList);
    }
}
