package com.datatom.dspool.utils;

import com.alibaba.druid.util.JdbcUtils;
import org.apache.hive.jdbc.HiveConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class Common {

    /**
     * 请求返回code值，代表正常返回
     */
    public static final int SUCCESS = 200;
    /**
     * 请求返回code值，代表程序出错
     */
    public static final int ERROR = 500;

    public static Pattern getDatePattern = Pattern.compile("getdate\\('.*?','.*?'\\)");
    public static Pattern currentTimeStampPattern = Pattern.compile("CURRENT_TIMESTAMP");
    public static Pattern maxTimePattern = Pattern.compile("MAX\\(time\\)");

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:hive2://dn9033:2181,dn9034:2181,dn9035:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";
        // 加载驱动类
        try {
            Class.forName(JdbcUtils.getDriverClassName(jdbcUrl));
            // 设定超时时间未15秒，v4.5.2 需求24修改
            Connection con = DriverManager
                    .getConnection(jdbcUrl, "hive", "");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
