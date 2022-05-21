package com.itheima.reggie.common;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 17:53
 * @description:基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
