package com.datatom.dspool.service.impl;

import com.datatom.dspool.mapper.PoolMapper;
import com.datatom.dspool.service.PoolService;
import com.datatom.dspool.utils.Common;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Service
public class PoolServiceImpl implements PoolService {
    @Resource
    PoolMapper poolMapper;

    @Override
    public Map<String, Map<String, Map<String, String>>> doSelect(String sqlString) {

        //正则替换，去除单行和多行注释，只保留需要执行的sql语句
        sqlString = sqlString.replaceAll("(--.*)|((/\\*)+?[\\w\\W]+?(\\*/)+)", "");
        // 通过；分割sql，方便之后遍历执行
        String[] sqlArray = sqlString.split(";");
        // 计数，存储本次执行了多少个sql
        int sqlNum = 0;
        Map<String, Map<String, Map<String, String>>> resultMap = new HashMap<>(16);
        for (String sql : sqlArray) {
            // 检查分割出的sql是否为空，若为空不执行查询操作
            if (sql.replaceAll(" ", "").length() != 0) {
                Map<String, Map<String, String>> sqlMap = new HashMap<>(16);
                // 执行sql查询语句
                List<Map<String, Object>> resultList = poolMapper.select( sql);

                for (int i = 0; i < resultList.size(); i++) {
                    // 遍历数据重构结构以对齐 python 中pandas DataFrame的格式
                    for (Map.Entry<String, Object> entry : resultList.get(i).entrySet()) {
                        // 判断key是否已存在，若不存在则新建map对象并赋值，否则反之
                        if (sqlMap.containsKey(entry.getKey())) {
                            Map<String, String> map = sqlMap.get(entry.getKey());
                            map.put(String.valueOf(i), String.valueOf(entry.getValue()));
                            sqlMap.put(entry.getKey(), map);
                        } else {
                            Map<String, String> map = new HashMap<>(16);
                            map.put(String.valueOf(i), String.valueOf(entry.getValue()));
                            sqlMap.put(entry.getKey(), map);
                        }
                    }
                }
                // 将该sql赋值回结果集中
                resultMap.put(String.valueOf(sqlNum), sqlMap);
                sqlNum += 1;
            }
        }

        return resultMap;
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
            List<Map<String,Object>> resultList = poolMapper.select(sql);
            resultList.get(0).get("result");
        }
    }

}
