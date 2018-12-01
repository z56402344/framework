package com.k12app.db.dao;

import android.text.TextUtils;

import com.k12app.bean.UserBean;
import com.k12app.common.GlobaleParms;
import com.k12lib.afast.log.Logger;

import z.db.ShareDB;

import static com.k12app.core.UserLogin.Auto_Login;

/**
 * 说明：本地对用户信息操作的类
 */
public interface IUser {
    String SEC_USERBEAN = "userbean";
    String SEC_AUTOLOGIN = "userinfo";

    //用户id
    String STUDENT_NO = "student_no";
    // 昵称
    String NAME = "name";
    //用户登录标识
    String TOKEN = "token";

    //手机号
    String MOBILE = "mobile";
    //密码
    String PWD = "password";
    //性别 1-男 2-女
    String GENDER = "gender";
    //头像
    String AVATAR = "head_img_url";
    //城市id
    String CITY_ID = "city_id";
    //城市名称
    String CITY_NAME = "city_name";
    //学校
    String SCHOOL = "school";
    //年级
    String GRADE = "grade";
    //我的班级
    String MYCLASS = "class";
    //钱包余额
    String BALANCE = "balance";
    //代金券数量
    String COUPON_NUM = "coupon_num";
    //消息免打扰设置  0-关闭 1-开启
    String DISTURB = "disturb";
    //是否付费
    String VIP = "vip";

//            "teacher_no":"xxx",
//            "period_low":"xxx",
//            "period_high":"xxx",
//            "subject":"xxx",
//            "cert_result":"xxx"
//            "honor":"xxx",
//            "intro":"xxx",
    //用户-老师ID
    String TEACHER_NO = "teacher_no";
    //老师低段位
     String PERIOD_LOW = "period_low";
    //老师高段位
     String PERIOD_HIGH = "period_high";
    //老师擅长科目
     String SUBJECT = "subject";
    //资格证url(教师资格证)
     String CERT_RESULT = "cert_result";
    //其他荣誉证书
     String HONOR = "honor";
    //老师简介
     String INTRO = "intro";
    //老师星星数
    String TEACHER_STARS = "teacher_stars";


    class Dao {
        private static UserBean mUser = null;

        // 获取用户
        public static UserBean getUser() {
            if (mUser == null) {
                mUser = new UserBean();
                ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
                mUser.fromSec(sec);
            }
            return mUser;
        }

        public static String getUserId() {
            return UserBean.getId();
        }

        // 检查用户是否登录
        public static boolean checkAutoLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            return sec.getBoolean("autologin");
        }

        public static void clearUser() {
            ShareDB.Sec.clearSec(SEC_USERBEAN);
        }

        public static void saveUser() {
            if (mUser == null) return;
            saveUser(mUser);
        }

        // 保存用户
        public static void saveUser(UserBean user) {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_USERBEAN);
            if (GlobaleParms.isTeacher){
                if (!TextUtils.isEmpty(user.teacher_no)) {
                    String oldId = sec.getString(IUser.TEACHER_NO);
                    if (oldId == null || !oldId.equals(user.teacher_no)) {
                        clearUser();
                        sec.clearAttrs();
                    }
                } else {
                    //防止id为空
                    user.teacher_no = sec.getString(TEACHER_NO);
                }
            }else{
                if (!TextUtils.isEmpty(user.student_no)) {
                    String oldId = sec.getString(IUser.STUDENT_NO);
                    if (oldId == null || !oldId.equals(user.student_no)) {
                        clearUser();
                        sec.clearAttrs();
                    }
                } else {
                    //防止id为空
                    user.student_no = sec.getString(STUDENT_NO);
                }
            }

            user.toSec(sec);
            sec.save(false);
            if (mUser != null && user != mUser) {
                mUser.fromSec(sec);
            }
        }

        public static void exitUser() {
            clearUser();
            if (mUser != null) {
                // 保留id值不清除
                if (GlobaleParms.isTeacher){
                    ShareDB.Key.update(SEC_USERBEAN, TEACHER_NO, mUser.teacher_no);
                }else {
                    ShareDB.Key.update(SEC_USERBEAN, STUDENT_NO, mUser.student_no);
                }
                mUser = null;
            }
        }

        // 更新用户头像
        public static void updateAvatar(String avatar) {
            UserBean user = getUser();
            if (user.head_img_url != null && user.head_img_url.equals(avatar)) return;
            user.head_img_url = avatar;
            ShareDB.Key.update(SEC_USERBEAN, AVATAR, avatar);
        }

        // 更新用户性别
        public static void updateSex(int gender) {
            UserBean user = getUser();
            if (user.gender != -1 && user.gender==gender) return;
            user.gender = gender;
            ShareDB.Key.update(SEC_USERBEAN, GENDER, gender);
        }

        public static void updateLogin(int status) {
            UserBean user = getUser();
            if (user == null || user.login == status) return;
            user.login = status;
            ShareDB.Key.update(SEC_AUTOLOGIN, "login", status);
        }

        public static boolean isLogin() {
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            sec.load();
            return sec.getInt("login") > 0;
        }

        // 更新用户头像
        public static void updataImage(String avatar) {
            UserBean user = getUser();
            if (user.head_img_url != null && user.head_img_url.equals(avatar)) return;
            user.head_img_url = avatar;
            ShareDB.Key.update(SEC_USERBEAN, AVATAR, avatar);

        }

        //----------------k12----------------
        public static void updateToken(String token) {
            UserBean user = getUser();
            if (user != null){
                user.token = token;
            }
            Logger.i("注册完成后更新token >>> "+token);
            ShareDB.Key.update(SEC_AUTOLOGIN, IUser.TOKEN, token);
            ShareDB.Key.update(SEC_USERBEAN, IUser.TOKEN, token);
            //saveUser(user);
        }

        public static String getToken() {
            UserBean user = getUser();
            if (user == null) return null;
            return user.token;
        }

        public static void updateAccAndPwd(String acc, String pwd){
//            ShareDB.Key.update(SEC_AUTOLOGIN, MOBILE, acc);
//            ShareDB.Key.update(SEC_AUTOLOGIN, PWD, pwd);
            ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
            sec.put(Auto_Login, true);
            sec.put(IUser.MOBILE, acc);
            sec.put(IUser.PWD, pwd);
            sec.save(false);
        }
    }
}
