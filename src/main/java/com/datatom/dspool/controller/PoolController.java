package com.datatom.dspool.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.alibaba.druid.util.JdbcUtils;
import com.datatom.dspool.service.PoolService;
import com.datatom.dspool.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Controller
@RequestMapping("/pool")
public class PoolController {
    private static Logger logger = LoggerFactory.getLogger(PoolController.class);
    @Resource
    PoolService poolService;


    @RequestMapping("/executeSql")
    @ResponseBody
    public Object getRequest(@RequestParam("file") MultipartFile file) {

        Map<String, Object> resultMap = new HashMap<>(16);
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
            resultMap.put("code", Common.SUCCESS);
            resultMap.put("data", poolService.runSqlAsPandasReturn(sqlString));
            resultMap.put("msg", "successful");
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", Common.ERROR);
            resultMap.put("msg", "sql 执行异常\r\n," + e.toString());

            logger.error("sql执行异常", e);
            return resultMap;
        }
    }

    @RequestMapping("/oracle/executeSql")
    @ResponseBody
    public Object getRequest(String sql) {
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            // 转为UTF-8编码
            resultMap.put("code", Common.SUCCESS);
            resultMap.put("data", poolService.runSqlAsXormReturn(sql));
            resultMap.put("msg", "successful");
            return resultMap;
        } catch (Exception e) {
            resultMap.put("code", Common.ERROR);
            resultMap.put("msg", "sql 执行异常\r\n," + e.toString());

            logger.error("sql执行异常", e);
            return resultMap;
        }
    }

    @RequestMapping("/checkConnect")
    @ResponseBody
    public static Object checkConnect(String jdbcUrl, String username, String password) {
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            // 加载驱动类
            Class.forName(JdbcUtils.getDriverClassName(jdbcUrl));
            Connection con = DriverManager
                    .getConnection(jdbcUrl, username, password);
            if (con != null) {
                con.close();
                resultMap.put("code", Common.SUCCESS);
                resultMap.put("data", true);
                resultMap.put("msg", "连接成功");
            } else {
                resultMap.put("code", Common.ERROR);
                resultMap.put("data", false);
                resultMap.put("msg", "未能创建连接，请检测连接信息");
            }

            return resultMap;
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("检查数据源是否能可用时异常", e);
            resultMap.put("code", Common.ERROR);
            resultMap.put("data", false);
            resultMap.put("msg", "创建连接时报错，"+e.getMessage());
            return resultMap;
        }
    }

    @RequestMapping("/d")
    public Object geta() {
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
