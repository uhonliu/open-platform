package com.opencloud.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuyadu
 */
@Slf4j
public class CryptoUtils {
    public static void main(String[] args) {
        String clientSecret = "0osTIhce7uPvDKHz6aa67bhCukaKoYl4";
        //参数签名算法测试例子
        HashMap<String, Object> cryptoMap = new HashMap<>(0);
        cryptoMap.put("username", "admin");
        cryptoMap.put("password", "123456");
        String data = CryptoUtils.encrypt(cryptoMap, clientSecret, CryptoType.AES);
        System.out.println("加密结果:" + data);
        System.out.println("解密结果:" + CryptoUtils.decrypt(data, clientSecret, CryptoType.AES));
        HashMap<String, Object> cryptoMap2 = new HashMap<>(0);
        cryptoMap2.put("phoneNum", "18718518094");
        cryptoMap2.put("signName", "跨境知道");
        cryptoMap2.put("tplCode", "SMS_142475098");
        JSONObject obj = new JSONObject();
        obj.put("code", "123456");
        cryptoMap2.put("tplParams", obj.toJSONString());
        String data2 = CryptoUtils.encrypt(cryptoMap2, clientSecret, CryptoType.AES);
        System.out.println("加密结果:" + data2);
        System.out.println("解密结果:" + CryptoUtils.decrypt(data2, clientSecret, CryptoType.AES));
    }

    /**
     * 加密
     *
     * @param paramMap     参数集合不含clientSecret
     * @param clientSecret 验证接口的clientSecret
     * @param type         加密类型
     * @return
     */
    public static String encrypt(Map<String, Object> paramMap, String clientSecret, CryptoType type) {
        if (paramMap == null || paramMap.isEmpty()) {
            return "";
        }
        String paramString = JSONObject.toJSONString(paramMap);
        return encrypt(paramString, clientSecret, type);
    }

    public static String encrypt(String paramString, String clientSecret, CryptoType type) {
        String encryptStr = "";
        if (StringUtils.isNotEmpty(paramString)) {
            if (type == null) {
                type = CryptoType.RSA;
            }

            //加密
            switch (type) {
                case DES:
                    encryptStr = EncryptUtils.encryptDES(paramString, clientSecret);
                    break;
                case AES:
                    encryptStr = EncryptUtils.encryptAES(paramString, clientSecret);
                    break;
                case RSA:
                    try {
                        encryptStr = RSAUtils.encryptByPublicKey(paramString, clientSecret);
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                    break;
                default:
                    break;
            }
        }

        return encryptStr;
    }

    /**
     * 解密
     *
     * @param paramString  必须包含
     * @param clientSecret 验证接口的clientSecret
     * @param type         加密类型
     * @return
     */
    public static Map<String, String> decryptToMap(String paramString, String clientSecret, CryptoType type) {
        Map<String, String> paramMap = new HashMap<>(0);
        String decryptStr = decrypt(paramString, clientSecret, type);
        if (StringUtils.isNotEmpty(decryptStr)) {
            paramMap = JSONObject.parseObject(decryptStr, new TypeReference<Map<String, String>>() {
            });
        }
        return paramMap;
    }

    public static String decrypt(String paramString, String clientSecret, CryptoType type) {
        String decryptStr = "";
        if (StringUtils.isNotEmpty(paramString)) {
            if (type == null) {
                type = CryptoType.RSA;
            }

            //加密
            switch (type) {
                case DES:
                    decryptStr = EncryptUtils.decryptDES(paramString, clientSecret);
                    break;
                case AES:
                    decryptStr = EncryptUtils.decryptAES(paramString, clientSecret);
                    break;
                case RSA:
                    try {
                        decryptStr = RSAUtils.decryptByPublicKey(paramString, clientSecret);
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        log.error(e.getMessage());
                        return null;
                    }
                    break;
                default:
                    break;
            }
        }
        return decryptStr;
    }

    public enum CryptoType {
        DES,
        TripleDES,
        AES,
        RSA;

        public static boolean contains(String type) {
            for (CryptoType typeEnum : CryptoType.values()) {
                if (typeEnum.name().equals(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}
