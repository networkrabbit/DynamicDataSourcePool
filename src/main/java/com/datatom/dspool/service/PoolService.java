package com.datatom.dspool.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */

public interface PoolService {

    /**
     * running sql and get return
     *
     * @param sqlString 要执行的sql语句。多个sql时使用 ";"分隔
     * @return 返回查询的结果集,格式和 pandas.read_sql() 后 to_json() 的格式对齐 返回样例 {"0":{"col1":{"0":"val1","1":"val2"},"clo2":{"0":"val1","1":"val2"}}}
     * 最外层的 "0" 含义为第几个执行的sql
     */
    Map<String, Map<String, Map<String, Object>>> runSqlAsPandasReturn(String sqlString);

    /**
     * 执行sql并按特殊格式返回
     *
     * @param sqlString
     * @return 返回List 数组
     */
    List<String> runSqlAsXormReturn(String sqlString);


    /**
     * 获取字符串中所有的匹配内容
     * @param str 原始字符串
     * @param pattern 匹配规则
//     * @return 以列表形式返回所有匹配的内容
     */
    void runSpecialUDF(String str, Pattern pattern);
}
