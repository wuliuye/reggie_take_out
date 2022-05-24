package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     *
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 订单分页查询
     *
     * @param page
     * @param pageSize
     * @return java.util.List<com.itheima.reggie.dto.OrderDto>
     **/
    Page<OrderDto> page(int page, int pageSize);
}
