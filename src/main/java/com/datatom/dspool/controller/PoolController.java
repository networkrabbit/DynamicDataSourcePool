package com.datatom.dspool.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.datatom.dspool.datasource.DataSourceContextHolder;
import com.datatom.dspool.datasource.DynamicDataSource;
import com.datatom.dspool.interceptor.DataInterceptor;
import com.datatom.dspool.service.PoolService;
import com.datatom.dspool.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Controller
@RequestMapping("/pool")
public class PoolController {
    @Resource
    PoolService poolService;


    @RequestMapping("/select")
    @ResponseBody
    public Object getRequest(@RequestParam("file") MultipartFile file, String ip, String port, String schema, String catalog) {
        try {
            // 读文件流获取上传的sql
            InputStream fis = file.getInputStream();

            // 读输入流，将其转为string类型，方便之后处理使用
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            // 转为UTF-8编码
            String sqlString = result.toString(StandardCharsets.UTF_8.name());
            return poolService.doSelect(sqlString);
        } catch (Exception e) {
            // todo 修改 e.printStackTrace();为日志形式
            System.out.println(e.getMessage());
            return e.toString();
        }
    }

}
