package com.datatom.dspool.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author xiangluyao
 * @date 2019/12/17 11:52
 * @description RES 非对称加密工具类
 */
public class RsaUtil {
    private static Logger logger = LoggerFactory.getLogger(RsaUtil.class);
    private static final String CREATE = "create";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    private static final String PUBLIC_KEY_ENCRYPT = "pubEn";
    private static final String PUBLIC_KEY_DECRYPT = "pubDe";
    private static final String PRIVATE_KEY_ENCRYPT = "pvtEn";
    private static final String PRIVATE_KEY_DECRYPT = "pvtDe";
    private static final String PRIVATE_KEY_SIGN = "pvtSign";
    private static final String PUBLIC_KEY_VERIFY = "pubVerify";



    public static void main(String[] args) {

        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJYps1rtKCJ25a2U2dwrbT3etDU4wHPgHJKaAheqDjiHUh4dP1o9WqAdPREBHfalT8YYephab3GMw9whPII5R9COyzwIiRtQ7NcN7g34fEaPvDAH906i9q9DjCVP0OD9xVvvfSqDbLJet9uxnZugBVhDnY9hTT9vnfX7AgZRLubwIDAQAB";
        String text = "bcf42bc1922244afb43dcb2c6dac19e9_1581415452608_endTime=2020-02-10 00:00:00&flag=1&startTime=2020-01-31 00:00:00";
        try {
            String sign = "LYD0T3Rz1ZvyqginZDvsOinNqxDwcXPaX+ppQO06OrW7jM5W9MKl+ei7YfSupAzJxo2mzTsXQwtj3GCwRxx/H33ZqPrJfGlrTIRNXGEBTloyn+BSj3+/66tyY1IYGYjHALwC3cWaEcInfmxCL+71y7v+2xdqRJCFKjvOLEzOmmY=";
            System.out.println(verify(text,sign,str2PublicKey(key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String key = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAqZXnCi26R+JCTXFM/qzGUp6Ujp65u504f4P/TVixlzsNUkZxIXCfanoo+xa2jHLSjDBlJsdQqJmCtadxUqFYpQIDAQABAkBO7t7i9fXGj6GcuifEQlbtDbLHz0lT8hHiCGZEcgv2/1kZ35MzpP4hI2cJfdpCWgdA2D7pYhqvwuMgcQTc1HXhAiEA83hf1KiIV/mfw8LKhp5oiXBmtJJ31tF59FOFROEZc+8CIQCyUCBgBTnFRkY3/pWbA7CkU3M3lOf7IkWZgBwWK+WYqwIhAI1vFQRdbu7kROocozf2l7WXhSJKn7E7+QN07UspFHcjAiEApdaPxb4WdLtfI0TLr1hHOQ3D4rXC63Z8dL8JYDWDW/ECIEX0I1+HcY7XJq3yrRjpl2Thecc1LqH+Dhppek9EGE2s";
//        String text = "OYmlGUf2d1Niik/mNuU/n6pARSCFIjyYRpkJpWYUxVFfWc716xWhDKuq6z1sVqIKrFpwzUDUZ8v6U5DYqCmiTA==";
        args = new String[]{PRIVATE_KEY_ENCRYPT};
        switch (args[0]) {
            case CREATE:
                try {
                    createKey();
                    break;
                } catch (NoSuchAlgorithmException e) {
                    logger.error("创捷RSA密钥失败", e);
                }
                break;
            case PUBLIC_KEY_ENCRYPT:
                try {
                    publicKeyEncrypt(str2PublicKey(key), text);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                    logger.error("公钥加密失败", e);
                }
                break;
            case PUBLIC_KEY_DECRYPT:
                try {
                    System.out.println(publicKeyDecrypt(str2PublicKey(key), text));

                } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                    logger.error("公钥解密失败", e);
                }
                break;
            case PRIVATE_KEY_ENCRYPT:
                try {
                    privateKeyEncrypt(str2PrivateKey(key), text);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                    logger.error("私钥加密失败", e);
                }
                break;
            case PRIVATE_KEY_DECRYPT:
                try {
                    privateKeyDecrypt(str2PrivateKey(key), text);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                    logger.error("私钥解密失败", e);
                }
                break;
            default:
                break;
        }
    }


    /**
     *
     */
    public static void createKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator;
        keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        System.out.println("Public Key:" + Base64.encodeBase64String(rsaPublicKey.getEncoded()));
        System.out.println("Private Key:" + Base64.encodeBase64String(rsaPrivateKey.getEncoded()));
    }

    /**
     * rsa 公钥加密方法
     *
     * @param rsaPublicKey 公钥
     * @param str          需要加密的字符串
     */
    public static void publicKeyEncrypt(RSAPublicKey rsaPublicKey, String str) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey2);
        byte[] result = cipher.doFinal(str.getBytes());
        System.out.println("公钥加密、私钥解密 ---- 加密:" + Base64.encodeBase64String(result));
    }

    /**
     * 公钥解密方法
     *
     * @param rsaPublicKey rsa公钥
     * @param cipherText   需要解密的密文
     */
    public static String publicKeyDecrypt(RSAPublicKey rsaPublicKey, String cipherText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(cipherText));
        return new String(result);
    }

    /**
     * rsa私钥解密方法
     *
     * @param rsaPrivateKey rsa私钥
     * @param cipherText    需要解密的密文字符串
     */
    public static void privateKeyDecrypt(RSAPrivateKey rsaPrivateKey, String cipherText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(cipherText));
        System.out.println("公钥加密、私钥解密 ---- 解密:" + new String(result));
    }

    /**
     * rsa 私钥解密方法
     *
     * @param rsaPrivateKey rsa 私钥
     * @param str           字符串
     */
    public static void privateKeyEncrypt(RSAPrivateKey rsaPrivateKey, String str) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(str.getBytes());
        System.out.println("私钥加密、公钥解密 ---- 加密:" + Base64.encodeBase64String(result));
    }


    /**
     * 用私钥对信息生成数字签名
     * @param data       加密数据
     * @param privateKey 私钥
     * @return
     * @throws
     */
    public static String sign(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        // 解密由 base64 编码的私钥
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        // 构造 PKCS8EncodedKeySpec 对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(bytes);
        return Base64.encodeBase64String(signature.sign());
    }


    public static boolean verify(String message, String signature,RSAPublicKey rsaPublicKey) throws Exception {
        // 获取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
        sign.initVerify(publicKey);

        sign.update(message.getBytes(StandardCharsets.UTF_8));
        return sign.verify(Base64.decodeBase64(signature));
    }


    /**
     * 字符串转 RSAPublicKey 方法
     *
     * @param key 字符串
     * @return RSAPublicKey对象
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey str2PublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 字符串转 RSAPrivateKey 方法
     *
     * @param key 密钥的字符串
     * @return RSAPrivateKey
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey str2PrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }


}
