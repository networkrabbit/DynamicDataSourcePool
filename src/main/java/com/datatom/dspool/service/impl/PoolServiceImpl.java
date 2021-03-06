package com.datatom.dspool.service.impl;

import com.datatom.dspool.mapper.PoolMapper;
import com.datatom.dspool.service.PoolService;
import com.datatom.dspool.utils.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Service
public class PoolServiceImpl implements PoolService {
    private static Logger logger = LoggerFactory.getLogger(PoolServiceImpl.class);
    @Resource
    PoolMapper poolMapper;


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Map<String, Map<String, Map<String, Object>>> runSqlAsPandasReturn(String sqlString) {

        //正则替换，去除单行和多行注释，只保留需要执行的sql语句
        sqlString = sqlString.replaceAll("(--.*)|((/\\*)+?[\\w\\W]+?(\\*/)+)", "");
        // 通过；分割sql，方便之后遍历执行
        String[] sqlArray = sqlString.split(";");
        // 计数，存储本次执行了多少个sql
        int sqlNum = 0;
        Map<String, Map<String, Map<String, Object>>> resultMap = new HashMap<>(16);
        for (String sql : sqlArray) {
            // 检查分割出的sql是否为空，若为空不执行查询操作
            if (sql.trim().length() != 0) {
                Map<String, Map<String, Object>> sqlMap = new HashMap<>(16);
                String type = sql.trim().toLowerCase().split(" ")[0];
                logger.debug("sql type: " + type + " ,sql:" + sql);

                // 判断sql类型执行不同的方法
                if ("select".equals(type)) {
                    // 执行sql查询语句
                    List<Map<String, Object>> resultList = poolMapper.select(sql);

                    for (int i = 0; i < resultList.size(); i++) {
                        // 遍历数据重构结构以对齐 python 中pandas DataFrame的格式
                        for (Map.Entry<String, Object> entry : resultList.get(i).entrySet()) {
                            // 判断key是否已存在，若不存在则新建map对象并赋值，否则反之
                            if (sqlMap.containsKey(entry.getKey())) {
                                Map<String, Object> map = sqlMap.get(entry.getKey());
                                map.put(String.valueOf(i), entry.getValue());
                                sqlMap.put(entry.getKey(), map);
                            } else {
                                Map<String, Object> map = new HashMap<>(16);
                                map.put(String.valueOf(i), entry.getValue());
                                sqlMap.put(entry.getKey(), map);
                            }
                        }
                    }
                } else if ("insert".equals(type)) {
                    int rowNums = poolMapper.insert(sql);
                    Map<String, Object> rowMap = new HashMap<>(16);
                    rowMap.put("0", rowNums);
                    sqlMap.put("rows", rowMap);
                } else if ("delete".equals(type)) {
                    boolean success = poolMapper.delete(sql);
                    Map<String, Object> rowMap = new HashMap<>(16);
                    rowMap.put("0", null);
                    sqlMap.put("rows", rowMap);
                } else {
                    // 不支持的类型，单独处理返回的信息
                    Map<String, Object> errorMap = new HashMap<>(16);
                    errorMap.put("sql", sql);
                    errorMap.put("msg", "不支持的sql类型，现仅支持 select、insert、delete");
                    sqlMap.put("info", errorMap);
                }

                // 将该sql赋值回结果集中
                resultMap.put(String.valueOf(sqlNum), sqlMap);
                sqlNum += 1;
            }
        }

        return resultMap;
    }


    @Override
//    @Transactional(rollbackFor = RuntimeException.class)
    public List<Map<String, Object>> runSqlAsHiveReturn(String sqlString) {
        return poolMapper.updateStr(sqlString);
    }



    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<String> runSqlAsXormReturn(String sqlString) {
        // 为保持查询字段有序返回，使用 LinkedHashMap
        List<LinkedHashMap<String, Object>> resultList = poolMapper.selectStr(sqlString);

        List<String> returnList = new ArrayList<>();
        // 为将数据拼接为列表返回，原始使用时仅查一条数据，所以不存在多行数据的问题
        for (LinkedHashMap<String, Object> map : resultList) {
            // 注意！！！！！
            // 当单查一个字段，且字段值为null时，会返回一个 键值对都时null的对象
            // 为防止此类返回报错，单独处理
            if (map == null) {
                returnList.add("");
            } else {
                // 正常业务处理流程
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    // 对空值进行特殊处理，不让其转为 "null"
                    if (entry.getValue() == null) {
                        returnList.add("");
                    } else {
                        returnList.add(String.valueOf(entry.getValue()));
// todo 编码特殊转换
//                        try {
//                            byte[] s = (String.valueOf(entry.getValue())).getBytes("ISO-8859-1");
//                            returnList.add(new String((String.valueOf(entry.getValue())).getBytes("ISO-8859-1"), "GBK"));
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }


        }
        return returnList;
    }


    @Override
    public void runSpecialUDF(String str, Pattern pattern) {


        Matcher m = pattern.matcher(str);
        List<String> matchStrList = new ArrayList<>();
        //此处find（）每次被调用后，会偏移到下一个匹配
        if (pattern.equals(Common.maxTimePattern)) {
            while (m.find()) {
                //获取当前匹配的值
                matchStrList.add("CURRENT_TIMESTAMP");
            }
        } else {
            while (m.find()) {
                //获取当前匹配的值
                matchStrList.add(m.group());
            }
        }

        for (String matchStr : matchStrList) {
            String sql = String.format("select %s as result", matchStr);
            List<Map<String, Object>> resultList = poolMapper.select(sql);
            resultList.get(0).get("result");
        }
    }

}
