package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/24 21:01
 * @description:
 */
@Data
public class OrderDto extends Orders {


    /**
     * 订单明细
     */
    private List<OrderDetail> orderDetails;

    /**
     * 菜品总数量
     */
    private Integer sumNum;
}
