package com.datatom.dspool.utils;

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
    public static final int SUCCESS=200;
    /**
     * 请求返回code值，代表程序出错
     */
    public static final int ERROR=500;

    public static Pattern getDatePattern = Pattern.compile("getdate\\('.*?','.*?'\\)");
    public static Pattern currentTimeStampPattern = Pattern.compile("CURRENT_TIMESTAMP");
    public static Pattern maxTimePattern = Pattern.compile("MAX\\(time\\)");


}
