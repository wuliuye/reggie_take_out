package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.config.AliyunSmsParamConfig;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/23 20:33
 * @description:用户管理
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private AliyunSmsParamConfig aliyunSmsParamConfig;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送短信验证码
     *
     * @param user
     * @param request
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        log.info("user:{}", user);
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);
            //发送短信验证码
            //SMSUtils.sendMessage(aliyunSmsParamConfig.getAccessKeyId(), aliyunSmsParamConfig.getSecret(),
            //        aliyunSmsParamConfig.getSignName(), aliyunSmsParamConfig.getTemplateCode(), phone, code);
            //将生成的验证码保存到session中（key为手机号，value为验证码）
            //request.getSession().setAttribute(phone, code);
            //将生成的验证码保存到redis中（key为手机号，value为验证码）,设置1分钟有效期
            redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);
            return R.success("发送验证码成功");
        }
        return R.error("发送验证码失败");
    }

    /**
     * 用户登录
     *
     * @param map
     * @param request
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpServletRequest request) {

        //获取用户输入的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //根据用户输入的手机号去session中查询验证码
        //Object sessionCode = request.getSession().getAttribute(phone);
        //根据用户输入的手机号去redis中查询验证码
        Object sessionCode = redisTemplate.opsForValue().get(phone);

        //匹配验证码
        if (sessionCode != null && code.equals(sessionCode.toString())) {
            //判断用户是不是新用户，如果是新用户，自动给用户注册账号
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StringUtils.isNotBlank(phone), User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                //给新用户注册账号
                user = new User();
                //设置新用户的手机号，设置状态为可用
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将用户id设置进session中
            request.getSession().setAttribute("user", user.getId());

            //如果用户登录成功，删除redis中的key
            redisTemplate.delete(phone);
            //登录成功
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
