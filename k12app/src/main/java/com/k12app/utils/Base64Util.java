package com.k12app.utils;

import com.umeng.socialize.net.utils.Base64;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import Decoder.BASE64Encoder;

/**
 * Created by Du on 17/6/17.
 */

public class Base64Util {

    public static String getTranid(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String tranid = format.format(System.currentTimeMillis());
        tranid += String.format("%1$0"+(24-tranid.length())+"d",0);
        return tranid;
    }

    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    public static byte[] str2Byte(String base64Str){
        return Base64.decodeBase64(base64Str);
    }
    //byte[]转base64
    public static String byte2Str(byte[] b){
        return Base64.encodeBase64String(b);
    }
}
