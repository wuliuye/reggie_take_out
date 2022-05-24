package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/24 14:39
 * @description:购物车管理
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加菜品或者套餐到购物车
     *
     * @param shoppingCart
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {

        log.info("购物车数据:{}", shoppingCart);

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        //TODO 前端未做同种商品不同口味的设置，理论上如果已经存在该商品，还需要判断口味是否一致，不一致的话需要添加进购物车
        if (cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }


    /**
     * 或者用户的购物车列表
     *
     * @param
     * @return com.itheima.reggie.common.R<java.util.List < com.itheima.reggie.entity.ShoppingCart>>
     **/
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {

        log.info("查看购物车...");
        //获取用户Id
        Long userId = BaseContext.getCurrentId();
        //查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 减少购物车内商品数量
     *
     * @param shoppingCart
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        //要减少的是菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //要减少的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询要减少的菜品或者套餐在数据库的数量
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        //如果大于一件就减少数量
        if (cartServiceOne.getNumber() > 1) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果数据库只有一件该商品，则直接删除
            shoppingCartService.remove(queryWrapper);
        }
        
        return R.success("减少商品成功");
    }

    /**
     * 清空购物车
     *
     * @param
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @DeleteMapping("/clean")
    public R<String> clean() {
        //SQL:delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
