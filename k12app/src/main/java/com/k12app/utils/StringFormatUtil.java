package com.k12app.utils;

/**
 * 格式化字符串
 */
public class StringFormatUtil {

    public static String formBalance(String balance){
        return String.format("%1$.2f",balance) + "";
    }

    public static String formBalance(double balance){
        return String.format("%1$.2f",balance) + "";
    }
}
