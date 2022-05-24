package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:35
 * @description:套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐，同时保存套餐与菜品的关联
     *
     * @param setmealDto
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("setmealDto:{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<SetmealDto> setmealDtoPage = setmealService.page(page, pageSize, name);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        if (ids.size() > 0) {
            setmealService.removeWithDish(ids);
            return R.success("套餐删除成功");
        }
        return R.error("请勾选要删除的套餐");
    }

    /**
     * 根据套餐所属分类Id查询套餐列表
     *
     * @param setmeal
     * @return com.itheima.reggie.common.R<java.util.List < com.itheima.reggie.entity.Setmeal>>
     **/
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        //根据分类id查询套餐列表
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                //菜品处于起售状态
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
