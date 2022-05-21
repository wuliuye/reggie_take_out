package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 20:40
 * @description:(菜品或者套餐)分类服务
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id删除分类，删除之前需要判断该分类是否关联了菜品或者套餐
     *
     * @param id
     * @return void
     **/
    @Override
    public void remove(Long id) {

        //查询该分类是否关联了菜品，如果有则抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            //分类关联了菜品，不能删除，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询该分类是否关联了套餐，如果有则抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            //分类关联了套餐，不能删除，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //删除分类
        super.removeById(id);
    }
}
