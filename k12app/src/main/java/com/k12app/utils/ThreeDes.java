package com.k12app.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import Decoder.BASE64Encoder;

/**
 * 3DES
 */
public class ThreeDes {

    private static final String Algorithm = "DESede"; // DES,DESede,Blowfish
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String encode(String src, String key) {
        try {
            DESedeKeySpec spec = new DESedeKeySpec(key.getBytes(DEFAULT_CHARSET));
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Algorithm);
            SecretKey securekey = keyfactory.generateSecret(spec);

            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            byte[] b = cipher.doFinal(src.getBytes(DEFAULT_CHARSET));
            return new BASE64Encoder().encode(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String src, String key) throws Exception {
        //--通过base64,将字符串转成byte数组
        byte[] bytesrc = Base64Util.str2Byte(src);
        //--解密的key
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
        SecretKey securekey = keyFactory.generateSecret(dks);

        //--Chipher对象解密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);
    }

}
