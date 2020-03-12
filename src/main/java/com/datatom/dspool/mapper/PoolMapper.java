package com.datatom.dspool.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
@Mapper
public interface PoolMapper {
    /**
     * running sql and get return
     *
     * @param sql 需要查询的sql语句
     * @return 返回查询的结果集
     */
    @Select("${sql}")
    List<Map<String, Object>> select(String sql);

    /**
     * insert 方法
     *
     * @param sql 要执行的插入数据语句
     * @return 插入的行数
     */
    @Insert("${sql}")
    int insert(String sql);


    /**
     * delete 方法
     *
     * @param sql 要执行的删除数据语句
     * @return 是否成功
     */
    @Delete("${sql}")
    boolean delete(String sql);

    /**
     * running sql and get return
     *
     * @param sql 需要查询的sql语句
     * @return 返回查询的结果集
     */
    @Select("${sql}")
    List<LinkedHashMap<String, Object>> selectStr(String sql);

    /**
     * running sql and get return
     *
     * @param sql 需要查询的sql语句
     * @return 返回查询的结果集
     */
    @Select("${sql}")
    List<Map<String, Object>> updateStr(String sql);

}

