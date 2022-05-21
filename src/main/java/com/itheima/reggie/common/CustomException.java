package com.itheima.reggie.common;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 21:50
 * @description:自定义业务异常
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
