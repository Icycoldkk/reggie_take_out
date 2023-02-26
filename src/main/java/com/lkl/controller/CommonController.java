package com.lkl.controller;


import com.lkl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController
{
    //yml配置文件中的存放路径
    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload (MultipartFile file)
    {
        //用uuid代替原文件名防止相同
        String originalFilename = file.getOriginalFilename();
        originalFilename = originalFilename.substring(originalFilename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        originalFilename = uuid + originalFilename;


        //创建一个目录
        File dir = new File(basePath);
        if(!dir.exists())
        {
            //目录不存在则创建
            dir.mkdirs();
        }
        try
        {
            file.transferTo(new File(basePath + originalFilename));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return R.success(originalFilename);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response)
    {

        try
        {
            //输入流，读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("imag/jpeg");
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = fileInputStream.read(bytes)) != -1)
            {
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}
