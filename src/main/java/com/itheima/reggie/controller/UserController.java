package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.config.AliyunSmsParamConfig;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 发送短信验证码
     *
     * @param user
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
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
            return R.success("发送验证码成功");
        }
        return R.error("发送验证码失败");
    }
}
