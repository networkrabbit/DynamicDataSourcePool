package com.datatom.dspool.interceptor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatom.dspool.datasource.DataSourceContextHolder;
import com.datatom.dspool.datasource.DynamicDataSource;
import com.datatom.dspool.model.ReturnCodeType;
import com.datatom.dspool.utils.Common;
import com.datatom.dspool.utils.Md5;
import com.datatom.dspool.utils.RsaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class DataInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(DataInterceptor.class);

    private static final String TRUE = "true";
    @Value("${rsa.public-key}")
    private static String rsaPublicKey;
    @Value("${rsa.enable}")
    private static String rsaEnable;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object o) {
        String jdbcUrl = request.getParameter("jdbcUrl");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(jdbcUrl)) {
            // 返回错误信息给请求端解析
            sendJson(response, Common.ERROR, "参数中需要有 jdbcUrl");
            return false;
        }
        // 判断是否开启rsa 加解密验证
        if (TRUE.equals(rsaEnable)) {
            try {
                password = RsaUtil.publicKeyDecrypt(RsaUtil.str2PublicKey(rsaPublicKey), password);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                // 返回错误信息给请求端解析
                sendJson(response, Common.ERROR, "私钥加密，公钥解密  解密失败\r\n" + e.getMessage());
                logger.error("私钥加密，公钥解密  解密失败", e);
                return false;
            }
        }
        // 将字符串拼接转md5加密，通过比较md5值的一致性来判断数据源的一致性
        String key = Md5.md5(jdbcUrl + username + password, 16);

        // 判断该数据源是否已在map中存在,当不存在时添加新数据源,否则直接切换
        if (!DynamicDataSource.getInstance().getDataSourceMap().containsKey(key)) {
            try {
                createDataSource(jdbcUrl, username, password);
            } catch (SQLException e) {
                sendJson(response, Common.ERROR, "添加数据源失败\r\n" + e.getMessage());
                logger.error("添加的jdbc：" + jdbcUrl + "添加数据源失败", e);
                return false;
            }
        }
        // 切换数据源到新增数据源
        DataSourceContextHolder.setKey(key);
        logger.debug("切换数据源 md5 key:" + key + " jdbcUrl:" + jdbcUrl);
        return true;
    }

    /**
     * 新建数据源方法
     *
     * @param url      数据源jdbcURl
     * @param username 数据源登录用户名
     * @param password 数据源登录密码
     * @throws SQLException
     */
    private static void createDataSource(String url, String username, String password) throws SQLException {
        // 根据配置创建 DruidDataSource 对象
        DruidDataSource dynamicDataSource = new DruidDataSource();
        dynamicDataSource.setDriverClassName(JdbcUtils.getDriverClassName(url));
        dynamicDataSource.setUrl(url);
        dynamicDataSource.setUsername(username);
        dynamicDataSource.setPassword(password);

        // 将新建的数据源加载至动态数据源对象中
        String thisKey = Md5.md5(url + username + password, 16);
        Map<Object, Object> dataSourceMap = DynamicDataSource.getInstance().getDataSourceMap();
        dataSourceMap.put(thisKey, dynamicDataSource);
        DynamicDataSource.getInstance().setTargetDataSources(dataSourceMap);


    }

    /**
     * 回写json方法
     *
     * @param response HttpServletResponse
     * @param code     状态码
     * @param msg      返回的信息
     */
    private static void sendJson(HttpServletResponse response, int code, String msg) {
        PrintWriter out;
        try {
            out = response.getWriter();
            Map<String, Object> map = new HashMap<>(16);
            map.put("code", code);
            map.put("msg", msg);
            JSONObject json = (JSONObject) JSON.toJSON(map);
            out.append(json.toString());
        } catch (IOException e) {
            logger.error("获取response输出异常", e);
        }
    }

}
