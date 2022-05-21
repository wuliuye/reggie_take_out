package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:30
 * @description:
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
