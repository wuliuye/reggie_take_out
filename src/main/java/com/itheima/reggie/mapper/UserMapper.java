package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/23 17:58
 * @description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
