package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/21 20:41
 * @description:(套餐或者菜品)分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("category={}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

}
