package com.k12app.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 获取任意某天或者某几天的工具类
 */
public class DateUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String MM_DD = "MM-dd";
    public static final String MM月DD日 = "MM月dd日";
    public static ArrayList<String> mWeekList = new ArrayList<>();


    //获取过去任意天内的日期数组
    public static ArrayList<String> getPastDateList(int intervals,String pattern) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        mWeekList.clear();
        for (int i = 0; i <intervals; i++) {
            pastDaysList.add(getPastDate(i, pattern));
        }
        return pastDaysList;
    }

    //获取未来任意天内的日期数组
    public static ArrayList<String> getFetureDateList(int intervals,String pattern ) {
        ArrayList<String> fetureDaysList = new ArrayList<>();
        mWeekList.clear();
        for (int i = 0; i <intervals; i++) {
            fetureDaysList.add(getFetureDate(i, pattern));
        }
        return fetureDaysList;
    }

    //获取过去第几天的日期
    public static String getPastDate(int past,String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String result = format.format(today);
        return result;
    }

    //获取未来 第 past 天的日期
    public static String getFetureDate(int past,String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        mWeekList.add(getWeekOfDate(today));
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String result = format.format(today);
        return result;
    }

    public static ArrayList<String> getWeekList(){
        return mWeekList;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static int getYear(){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
        return c.get(Calendar.YEAR);    //获取年
    }
    public static int getHour(){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
        return c.get(Calendar.HOUR_OF_DAY);    //获取时
    }
    public static int getMinute(){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));    //获取东八区时间
        return c.get(Calendar.MINUTE);    //获取分
    }

}
