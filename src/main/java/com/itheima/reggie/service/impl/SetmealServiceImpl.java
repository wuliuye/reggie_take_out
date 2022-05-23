package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private CategoryService categoryService;

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

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.itheima.reggie.dto.SetmealDto>
     **/
    @Override
    public Page<SetmealDto> page(int page, int pageSize, String name) {

        //1.构造分页器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //2.构造查询条件(套餐名，起售状态)
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //3.按照套餐名进行查询套餐列表(如果有输入套餐名)
        queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name)
                //套餐处于起售状态
                .eq(Setmeal::getStatus, 1)
                //按照更新时间降序排序
                .orderByDesc(Setmeal::getUpdateTime);
        //4.查询
        super.page(setmealPage, queryWrapper);

        //5.对象拷贝
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        //6.List<Setmeal> --转--> List<SetmealDto> 同时设置SetmealDto的套餐分类名(categoryName)
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            //根据套餐分类id查询出套餐名(得到的参数是分类id，前台需要展示分类名)
            Category category = categoryService.getById(setmeal.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //分页对象设置记录
        setmealDtoPage.setRecords(setmealDtoList);
        return setmealDtoPage;
    }
}
