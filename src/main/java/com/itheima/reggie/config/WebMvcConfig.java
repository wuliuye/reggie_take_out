package com.itheima.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/20 19:39
 * @description:
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 设置静态资源映射，如果静态资源放到
     * "classpath:/META-INF/resources/"
     * "classpath:/resources/"
     * "classpath:/static/"
     * "classpath:/public/"
     * springboot默认可以访问静态资源的目录下，就不用设置这个
     * 正常情况下不用设置这个
     * @param registry
     * @return void
     **/
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射。。。。");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
