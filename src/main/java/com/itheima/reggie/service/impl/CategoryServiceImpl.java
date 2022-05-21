package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 20:40
 * @description:(菜品或者套餐)分类服务
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
