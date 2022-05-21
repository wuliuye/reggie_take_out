package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     **/
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
        //6.登录成功，将员工id放到session中并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);

    }

    /**
     * 员工退出
     *
     * @param request
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        //清除session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param request
     * @param employee
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        log.info("新增员工，员工信息:{}", employee.toString());

        //设置初始密码123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户id
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询(可通过name查询)
     *
     * @param page
     * @param pageSize
     * @param name
     * @return com.itheima.reggie.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     **/
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //分页构造器
        Page pageInfo = new Page(page, pageSize);
        //分页条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     *
     * @param employee
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("employee={}", employee);
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("员工信息更新成功");
    }

    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return com.itheima.reggie.common.R<com.itheima.reggie.entity.Employee>
     **/
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {

        log.info("根据id查询员工信息。。。");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
