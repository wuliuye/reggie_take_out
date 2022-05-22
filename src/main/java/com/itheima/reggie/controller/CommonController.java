package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author : wly
 * @version : 1.0
 * @date : 2022/5/22 11:47
 * @description:文件上传与下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return com.itheima.reggie.common.R<java.lang.String>
     **/
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        //file是一个临时文件，需要转存到其他位置，否则请求结束后就会被删除
        log.info("file:{}", file.toString());
        //获取原始文件名后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        //创建目录
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     * @return void
     **/
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        //输入流读取文件
        //输出流写回浏览器
        try (InputStream inputStream = new FileInputStream(new File(basePath + name));
             ServletOutputStream outputStream = response.getOutputStream()) {
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
