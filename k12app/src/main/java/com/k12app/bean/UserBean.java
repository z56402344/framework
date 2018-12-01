package com.k12app.bean;


import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.k12app.db.dao.IUser;

import java.io.Serializable;

import z.db.ShareDB;

/**
 * 说明：用户信息的Bean
 */
public class UserBean implements IUser {

//           "student_no":"xxx",
//            "head_img_url":"xxx",
//            "name": "xxx",
//            "mobile": "xxx",
//            "gender":"xxx",
//            "city_id":"xxx",
//            "city_name":"xxx",
//            "school":"xxx",
//            "grade":"xxx",
//            "class":"xxx",
//            "balance":"xxx",
//            "coupon_num":"xxx",
//            "disturb":"xxx"

    //老师多余字段
//            "teacher_no":"xxx",
//            "period_low":"xxx",
//            "period_high":"xxx",
//            "subject":"xxx",
//            "cert_result":"xxx"
//            "honor":"xxx",
//            "intro":"xxx",
//            teacher_stars

    //用户-学生ID
    public String student_no;
    //用户登录标识
    public String token;
    //用户昵称
    public String name;
    //用户联系方式
    public String mobile;
    //头像
    public String head_img_url;
    //性别1-男 2-女 3-保密
    public int gender = 1;
    //城市ID
    public String city_id;
    //城市名字
    public String city_name;
    //学校
    public String school;
    //年级
    public String grade;
    //班级
    @JSONField(name="class")
    public String myClass;
    //钱包余额
    public double balance = 0;//余额
    //优惠劵数量
    public String coupon_num;
    //消息免打扰设置  0-关闭 1-开启
    public int disturb;
    public int isFillInfo = 0;

    //用户-老师ID
    public String teacher_no;
    //老师低段位
    public String period_low;
    //老师高段位
    public String period_high;
    //老师擅长科目
    public int subject = -1;
    //资格证url(教师资格证) 0-普通待认证 1-专业 2-特约
    public int cert_result = -1;
    //其他荣誉证书
    public String honor;
    //老师简介
    public String intro;
    //老师星星数
    public double teacher_stars = -1;

    //-----------多余字段 start---------

    // 本地登录信息
    public int login = 0;
    //个人中心支付item 显示内容
    public PayTagBean payTag;

    //-----------多余字段 End ---------

    public static class PayTagBean implements Serializable {
        public String tag;
        public String title;
    }

    // 防止空id在其他模块出错
    public static String getId() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        String uid = sec.getString(STUDENT_NO);
        return TextUtils.isEmpty(uid) ? "null" : uid;
    }

    public static String getSex() {
        //1 男 0 女
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        return sec.getString(GENDER);
    }

    public static String getAvatar() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
        String avatar = sec.getString(AVATAR);
        return avatar;
    }

    public void toSec(ShareDB.Sec s) {
        if (!TextUtils.isEmpty(student_no)) s.put(STUDENT_NO, student_no);
        if (name != null) s.put(NAME, name);
        if (head_img_url != null) s.put(AVATAR, head_img_url);
        if (!TextUtils.isEmpty(token)) s.put(TOKEN, token);
        if (mobile != null) s.put(MOBILE, mobile);
        if (gender != -1) s.put(GENDER, gender);
        if (!TextUtils.isEmpty(city_id)) s.put(CITY_ID, city_id);
        if (!TextUtils.isEmpty(city_name)) s.put(CITY_NAME, city_name);
        if (!TextUtils.isEmpty(school)) s.put(SCHOOL, school);
        if (!TextUtils.isEmpty(grade)) s.put(GRADE, grade);
        if (!TextUtils.isEmpty(myClass)) s.put(MYCLASS, myClass);
        if (balance != -1) s.put(BALANCE, balance);
        if (!TextUtils.isEmpty(coupon_num)) s.put(COUPON_NUM, coupon_num);
        if (disturb != -1) s.put(DISTURB, disturb);

        if (!TextUtils.isEmpty(teacher_no)) s.put(TEACHER_NO, teacher_no);
        if (!TextUtils.isEmpty(period_low)) s.put(PERIOD_LOW, period_low);
        if (!TextUtils.isEmpty(period_high)) s.put(PERIOD_HIGH, period_high);
        if (subject != -1) s.put(SUBJECT, subject);
        if (cert_result != -1) s.put(CERT_RESULT, cert_result);
        if (!TextUtils.isEmpty(honor)) s.put(HONOR, honor);
        if (!TextUtils.isEmpty(intro)) s.put(INTRO, intro);
        if (teacher_stars != -1) s.put(CERT_RESULT, teacher_stars);

    }

    public void fromSec(ShareDB.Sec s) {
        student_no = s.getString(STUDENT_NO);
        token = s.getString(TOKEN);
        mobile = com(s.getString(MOBILE));
        name = com(s.getString(NAME));
        gender = s.getInt(GENDER);
        head_img_url = com(s.getString(AVATAR));
        city_id = com(s.getString(CITY_ID));
        city_name = com(s.getString(CITY_NAME));
        school = com(s.getString(SCHOOL));
        grade = com(s.getString(GRADE));
        myClass = com(s.getString(MYCLASS));
        balance = s.getDouble(BALANCE);
        coupon_num = com(s.getString(COUPON_NUM));
        disturb = s.getInt(DISTURB,0);

        teacher_no = s.getString(TEACHER_NO);
        period_low = s.getString(PERIOD_LOW);
        period_high = s.getString(PERIOD_HIGH);
        subject = s.getInt(SUBJECT);
        cert_result = s.getInt(CERT_RESULT);
        honor = s.getString(HONOR);
        intro = s.getString(INTRO);
        teacher_stars = s.getDouble(TEACHER_STARS);
    }

    //兼容空指针
    private String com(String s) {
        return s == null ? "" : s;
    }

}
