package com.datatom.dspool.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

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
     * @param sql 需要查询的sql语句
     * @return 返回查询的结果集
     */
    @Select("${sql}")
    List<Map<String,Object>> select( String sql);
}
