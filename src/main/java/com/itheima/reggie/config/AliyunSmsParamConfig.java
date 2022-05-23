package com.itheima.reggie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/23 21:05
 * @description:阿里云短信参数配置
 */
@Data
@Component
@ConfigurationProperties(value = "aliyun.sms")
public class AliyunSmsParamConfig {

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * secret
     */
    private String secret;

    /**
     * 短信签名名称，例如阿里云
     */
    private String signName;

    /**
     * 短信模板CODE,例如SMS_123456789
     */
    private String templateCode;
}
