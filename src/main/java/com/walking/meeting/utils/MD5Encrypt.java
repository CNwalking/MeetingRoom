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


    private static final String salt = "!!!@#$WALKING*SALT￥#@!!!";



    /**
     * 带秘钥加密
     * @param text 明文
     * @return 密文
     */
    public static String md5Encrypt(String text) {
        try {
        // 加密后的字符串
        String md5str = DigestUtils.md5Hex(text + salt);
        return md5str;
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
     * @return true/false
     * @throws Exception
     */
    public static Boolean verify(String text, String md5) {
        try {
        //根据传入的密钥进行验证
        String md5Text = md5Encrypt(text);
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
