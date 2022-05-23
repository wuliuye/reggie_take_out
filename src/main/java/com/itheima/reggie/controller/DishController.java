package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:34
 * @description:菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     *
     * @param dishDto
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //按照菜品名查询(如果有输入的话)
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        //查询
        dishService.page(dishPage, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //List<Dish> --转--> List<DishDto> 同时设置DishDto的分类名(categoryName)
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            //根据菜品里的菜品分类id查询出分类名(前台展示的是分类名)
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品id查询菜品信息，包括口味
     *
     * @param id
     * @return com.itheima.reggie.common.R<com.itheima.reggie.dto.DishDto>
     **/
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);

    }

    /**
     * 修改菜品信息,同时修改菜品对应菜品口味表数据
     *
     * @param dishDto
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        dishService.updateWithFlavor(dishDto);

        return R.success("菜品修改成功");
    }


    /**
     * 根据条件查询菜品列表
     *
     * @param dish
     * @return com.itheima.reggie.common.R<java.util.List < com.itheima.reggie.entity.Dish>>
     **/
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        //根据分类id查询菜品列表
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                //菜品处于起售状态
                .eq(Dish::getStatus,1)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
}