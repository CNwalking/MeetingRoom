package com.walking.meeting.utils;


import java.util.Objects;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * MD5加密 Util.
 */
@Slf4j
public class MD5Encrypt {


    private static final String HmacMD5 = "HmacMD5";

    /**
     * 生成一个AES密钥
     *
     * @return 密钥
     */
    public static String getKey(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(HmacMD5);
            keyGenerator.init(128);
            // 生成一个Key
            SecretKey generateKey = keyGenerator.generateKey();
            // 转变为字节数组
            byte[] encoded = generateKey.getEncoded();
            // 生成密钥字符串
            String encodeHexString = Hex.encodeHexString(encoded);
            return encodeHexString;
        } catch (Exception e) {
            log.error("获取密钥失败: {}" , e);
            return "";
        }
    }


    /**
     * 带秘钥加密
     * @param text 明文
     * @param key  密钥
     * @return 密文
     */
    public static String md5AndKey(String text, String key) {
        try {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text + key);
        return md5str;
        } catch (Exception e) {
            log.error("加密失败: {}" , e);
            return "";
        }
    }

    /**
     * MD5加密
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        try {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text);
        return DigestUtils.md5Hex(text);
        } catch (Exception e) {
            log.error("加密失败: {}" , e);
            return "";
        }
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param md5 密文
     * @param key 秘钥
     * @return true/false
     * @throws Exception
     */
    public static Boolean verify(String key, String text, String md5) {
        try {
        //根据传入的密钥进行验证
        String md5Text = md5AndKey(text,key);
        if(Objects.equals(md5Text, md5)){
            return true;
        }

        return false;
        } catch (Exception e) {
            log.error("加密失败: {}" , e);
            return false;
        }
    }



}
