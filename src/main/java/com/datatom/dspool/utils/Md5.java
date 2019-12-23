package com.datatom.dspool.utils;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description Md5 工具类
 */
public class Md5 {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * md5加密
     * @param info 要加密的消息
     * @param length 输出的md5类型（可选参数为16,32,64）
     * @return 加密后的数组
     */
    public static String md5(String info, int length) {
        if (length == 16) {
            return doMD5(info).substring(8, 24);
        } else if (length == 32) {
            return doMD5(info);
        } else if (length == 64) {
            Base64 base64 = new Base64();
            MessageDigest md5;
            String result = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
                result = base64.encodeToString(md5.digest(info.getBytes("utf-8")));
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 加密后的字符串
            return result;
        } else {
            return null;
        }
    }

    /**
     * md5加密
     * @param info 要加密的消息
     * @return 字母大写的加密串
     */
    public static String md5Upper(String info) {
        return doMD5(info).toUpperCase();
    }

    /**
     * 生成经过md5加密的字符串
     * @param info 要加密的字符串
     * @return 加密后的字符串
     */
    private static String doMD5(String info) {
        String result = null;
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            md.update(info.getBytes());
            // 获得密文
            byte[] byteArray = md.digest();
            // 把密文转换成十六进制的字符串形式
            result = byte2Hex(byteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * byte[] 转16进制字符串
     * @param byteArray byte[]
     * @return 字符串
     */
    private static String byte2Hex(byte[] byteArray) {
        // 一个byte为8位二进制，可以用两个十六进制位表示
        char[] hexChar = new char[byteArray.length * 2];
        int num = 0;
        int index = 0;
        for (byte b : byteArray) {
            if (b < 0) {
                num = b + 256;
            } else {
                num = b;
            }
            // 使用除与取余进行进制转换
            hexChar[index++] = HEX_CHAR[num / 16];
            hexChar[index++] = HEX_CHAR[num % 16];
        }
        return String.valueOf(hexChar);
    }


    public static void main(String[] args) {
        System.out.println(md5("11", 64));
    }


}
