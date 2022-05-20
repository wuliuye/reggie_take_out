package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/20 21:10
 * @description:
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
