package com.datatom.dspool.datasource;


/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public class DataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    public static synchronized void setKey(String key){
        CONTEXT_HOLDER.set(key);
    }
    public static String getKey(){
        return CONTEXT_HOLDER.get();
    }
    public static void clearKey(){
        CONTEXT_HOLDER.remove();
    }
}
