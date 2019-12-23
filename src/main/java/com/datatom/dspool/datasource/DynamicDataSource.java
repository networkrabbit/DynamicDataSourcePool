package com.datatom.dspool.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static DynamicDataSource instance;
    private static final byte[] LOCK=new byte[0];
    private static Map<Object,Object> dataSourceMap=new HashMap<Object, Object>();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        dataSourceMap.putAll(targetDataSources);
        // 必须添加该句，让方法根据重新赋值的targetDataSource依次根据key关键字
        // 查找数据源,返回DataSource,否则新添加数据源无法识别到
        super.afterPropertiesSet();
    }


    public Map<Object, Object> getDataSourceMap() {
        return dataSourceMap;
    }

    public static synchronized DynamicDataSource getInstance(){
        if(instance==null){
            synchronized (LOCK){
                if(instance==null){
                    instance=new DynamicDataSource();
                }
            }
        }
        return instance;
    }

    /**
     * 实现其抽象方法,
     * 因为在创建DataSource这个方法:determineTargetDataSource()中
     * 会调用这个key关键字,根据这个key在重新赋值的targetDataSource里面找DataSource
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String key = DataSourceContextHolder.getKey();
        DataSourceContextHolder.clearKey();
        return key;
    }
}
