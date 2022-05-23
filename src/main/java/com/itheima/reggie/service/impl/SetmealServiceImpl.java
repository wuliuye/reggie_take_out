package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:33
 * @description:套餐服务
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐与菜品的关联
     *
     * @param setmealDto
     * @return void
     **/
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        //每个SetmealDish设置套餐id
        setmealDishList = setmealDishList.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());

        //保存套餐与菜品关联
        setmealDishService.saveBatch(setmealDishList);
    }
}
