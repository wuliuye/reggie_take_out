package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/24 17:42
 * @description:订单服务
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * 前台传过来 地址id->addressBookId,支付方式->payMethod,备注->remark
     *
     * @param orders
     * @return void
     **/
    @Transactional
    public void submit(Orders orders) {

        //1.获取用户id
        Long userId = BaseContext.getCurrentId();
        //2.获取用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        //如果购物车为空
        if (shoppingCartList.size() == 0 || shoppingCartList == null) {
            throw new CustomException("购物车为空,不能下单");
        }
        //3.查询用户数据
        User user = userService.getById(userId);

        //4.根据前台传来的addressBookId去查询地址数据是否存在
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        //如果地址为空
        if (addressBook == null) {
            throw new CustomException("地址信息有误，不能下单");
        }

        //5.插入订单表(一条数据)
        //5.1 订单号
        long orderId = IdWorker.getId();

        //5.2 计算订单总价
        AtomicInteger amount = new AtomicInteger(0);
        //5.3 填充订单明细
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(item -> {

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            orderDetail.setImage(item.getImage());
            //把购物车每一项商品的总价加起来
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //5.4 填充订单
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //5.5 插入订单
        this.save(orders);

        //6.插入订单明细表(可能有多条)
        orderDetailService.saveBatch(orderDetailList);
        //7.删除用户购物车数据
        shoppingCartService.remove(queryWrapper);

    }
}