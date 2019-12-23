package com.datatom.dspool.interceptor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSONObject;
import com.datatom.dspool.datasource.DataSourceContextHolder;
import com.datatom.dspool.datasource.DynamicDataSource;
import com.datatom.dspool.utils.Common;
import com.datatom.dspool.utils.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class DataInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(DataInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object o) {
        String jdbcUrl = request.getHeader("jdbcUrl");
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        String key = Md5.md5(jdbcUrl + username + password,16);

        // 判断该数据源是否已在map中存在,当不存在时添加新数据源,否则直接切换
        if (!DynamicDataSource.getInstance().getDataSourceMap().containsKey(key)) {

            try {

                createDataSource(jdbcUrl, username, password);

            } catch (SQLException e) {
                logger.error(e.getMessage());
                sendJson(response,e.getMessage());
                return false;
            }
        }
        // 切换数据源到新增数据源
        DataSourceContextHolder.setKey(key);
        logger.info("切换数据源 md5 key:" + key +" jdbcUrl:"+jdbcUrl);

        return true;
    }

    private static void createDataSource(String url, String username, String password) throws SQLException {
        // 根据配置创建 DruidDataSource 对象
        DruidDataSource dynamicDataSource = new DruidDataSource();

        dynamicDataSource.setDriverClassName(JdbcUtils.getDriverClassName(url));
        dynamicDataSource.setUrl(url);
        dynamicDataSource.setUsername(username);
        dynamicDataSource.setPassword(password);

        // 将新建的数据源加载至动态数据源对象中
        String thisKey = Md5.md5(url + username + password,16);
        Map<Object, Object> dataSourceMap = DynamicDataSource.getInstance().getDataSourceMap();
        dataSourceMap.put(thisKey, dynamicDataSource);
        DynamicDataSource.getInstance().setTargetDataSources(dataSourceMap);


    }

    private static void sendJson(HttpServletResponse response,Object obj){
        PrintWriter out = null ;
        try{
            out = response.getWriter();
            JSONObject res =  JSONObject.parseObject(obj.toString());;
            out.append(res.toString());
        }
        catch (IOException e){
            logger.error(e.getMessage());
        }
    }

}
