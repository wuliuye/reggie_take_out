package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/20 21:13
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1.密码md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("加密后的密码：{}", password);

        //2.根据用户名查询数据库是否有该员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.没有查询到返回失败结果
        if (emp == null) {
            return R.error("登录失败");
        }
        //4.密码不一致
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //5.该员工被禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //6.登录成功，将用户id放到session中并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }
}
