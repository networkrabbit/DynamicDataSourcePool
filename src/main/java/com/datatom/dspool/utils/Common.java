package com.datatom.dspool.utils;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description
 */
public  class Common {

    public static Pattern getDatePattern = Pattern.compile("getdate\\('.*?','.*?'\\)");
    public static Pattern currentTimeStampPattern = Pattern.compile("CURRENT_TIMESTAMP");
    public static Pattern maxTimePattern = Pattern.compile("MAX\\(time\\)");

    public static String getMD5(byte[] source) {
        String s = null;
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        // 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            // MD5 的计算结果是一个 128 位的长整数，
            byte[] tmp = md.digest();
            // 用字节表示就是 16 个字节
            // 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
            char[] str = new char[16 * 2];
            // 进制需要 32 个字符
            // 表示转换结果中对应的字符位置
            int k = 0;
            // 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
            for (int i = 0; i < 16; i++) {
                // 进制字符的转换
                // 取第 i 个字节
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                // 取字节中高 4 位的数字转换,// >>> // 为逻辑右移，将符号位一起右移
                // 取字节中低 4 位的数字转换
                str[k++] = hexDigits[byte0 & 0xf];
            }
            // 换后的结果转换为字符串
            s = new String(str);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }
}
