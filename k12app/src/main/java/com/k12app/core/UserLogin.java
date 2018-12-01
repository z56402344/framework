package com.k12app.core;


import android.content.Context;
import android.text.TextUtils;

import com.k12app.bean.UserBean;
import com.k12app.common.ContantValue;
import com.k12app.db.dao.IUser;
import com.k12app.frag.login.PerfectInfoFrag;
import com.k12app.frag.main.HomeFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.net.SyncMgr;
import com.k12lib.afast.log.Logger;
import com.k12lib.afast.utils.ToastUtils;

import z.db.ShareDB;
import z.frame.BaseFragment;
import z.frame.ICommon;
import z.frame.LocalCenter;

public class UserLogin implements HttpItem.IAllLis, IKey, ICommon, IUser, IAct {

    public static final int FID = FidAll.UserLogin;
    private static final String TAG = UserLogin.class.getSimpleName();
    public static String Auto_Login = "autologin";
    public static String Login_type = "login_type";
    public static boolean isLogining = false;//是否正在登录
    public boolean isNormalLogin = false;//true==普通方式, false ==自动登录方式
    public static final int Login_Success = FID + 1;


    // 清除自动登录用户
    public static void clearUser() {
        // 清除自动登录信息
        ShareDB.Sec.clearSec(SEC_AUTOLOGIN);
        // 清除用户资料信息
        IUser.Dao.exitUser();
    }

    private HttpItem hi = new HttpItem();
    private Context mCtx;
    private String mUser; // 用户标示
    private String mPwd; // 用户密码/token
    private static int mLoginType = 0; //登陆类型,0-正常密码登录, 1-动态密码登录

    private BaseFragment mBfg;

    public UserLogin(Context ctx) {
        mCtx = ctx;
        // 从cookies获取登录状态
//		IUser.Dao.updateLogin(0);
    }

    public void setBaseFragment(BaseFragment bfg) {
        mBfg = bfg;
    }

    private void showShortToast(String txt) {
        if (mCtx == null) return;
        ToastUtils.showShortToast(txt);
    }

    public static void saveData(String account, String pwd, UserBean userInfo) {
        // 初始化内存变量
        // 重新打开数据库
        app.db.reOpen();
        // 保存到数据库
        IUser.Dao.saveUser(userInfo);
        // 保存到自动登录配置
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        sec.put(Auto_Login, true);
        sec.put(IUser.MOBILE, account);
        if (pwd != null) sec.put(IUser.PWD, pwd);
        sec.put(IUser.TOKEN, userInfo.token != null ? userInfo.token : "");
        sec.put(IUser.STUDENT_NO, userInfo.student_no);
        sec.put(IUser.AVATAR, userInfo.head_img_url);
        sec.put("login", 1);
        sec.put("autoLoginTime", SyncMgr.curSec());
        sec.put("login_type",mLoginType);
        sec.save(false);
    }

    public UserLogin setLoginType(int loginType){
        mLoginType = loginType;
        return this;
    }
    // 执行登录
    public void doLogin(String user, String pwd) {
//        String cid = ShareDB.Key.loadString(IAutoParams.kSec, IAutoParams.kPushId);
        String cid = null;
        doLogin(user, pwd, mLoginType);
    }

    public static void autoLogin() {
//        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
//        int lastUp = sec.getInt("autoLoginTime");
//        int cur = SyncMgr.curSec(); // 秒
//        int checkTime = 60 * 60 * 1; // 失效时间1小时
//        if (cur < lastUp + checkTime) {
//            Logger.e("自动登录 >> 未到1小时");
//            return;
//        }
        if (isLogining) return;
        UserLogin userLogin = new UserLogin(app.ctx);
        userLogin.doAutoLogin();
    }

    //强制刷新一次用户信息
    public static void forceAutoLogin() {
        if (isLogining) return;
        UserLogin userLogin = new UserLogin(app.ctx);
        userLogin.doAutoLogin();
    }

    private void doLogin(String account, String passWord, int loginType) {
        mUser = account;
        mPwd = passWord;
        mLoginType = loginType;
        isLogining = true;
        hi.setId(HttpLogin);
        hi.setUrl(ContantValue.F_LOGIN)
                .put("mobile", account)
                .put("password", passWord)
                .put(Login_type,mLoginType);
        hi.setListener(this).post(null);
    }

    // 执行自动登录
    public void doAutoLogin() {
        ShareDB.Sec sec = new ShareDB.Sec(SEC_AUTOLOGIN);
        if (!sec.getBoolean(Auto_Login)) return;

        String account = sec.getString(IUser.MOBILE);
        String pwd = sec.getString(IUser.PWD);
        mLoginType = sec.getInt(Login_type,0);
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd) || mLoginType == 1) {
            Logger.i(TAG, "自动登录  账号或密码为空,跳转到登录页面 >>> 账号=" + account + " ,密码=" + pwd );
            LocalCenter.send(HomeFrag.IA_AutoLogin, 0, 0);
        } else {
            Logger.i(TAG, "自动登录中... >>>");
            doLogin(account, pwd, mLoginType);
        }
    }

    // 执行注销接口
    public void doLogout() {
        new HttpItem().setId(HttpLogout).setUrl(ContantValue.F_LOGOUT).setListener(this).post(this);
    }

    // 登录和注销
    private static final int HttpLogout = 10000;
    private static final int HttpLogin = 10001;

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        if (mBfg != null) {
            mBfg.hideLoading();
        }
        switch (resp.id) {
            case HttpLogout:
                clearUser();
                break;
            case HttpLogin:
                isLogining = false;
                UserBean userInfo = resp.getObject(UserBean.class, RES);
                if (userInfo == null) {
                    showShortToast(FMTERR);
                    return false;
                }
//            // 设置登录状态
//                IUser.Dao.updateLogin(1);
                saveData(mUser, mPwd, userInfo);
                //TODO 这个需要看一下干嘛的
//            SyncMgr.handleLoginOK();//这个需要看一下干嘛的
                Logger.i(TAG, "登录成功 >>> ");

                //登录接口成功时，判断该用户是否已完善个人资料
                if (Dao.getUser().isFillInfo != 0) {//跳转至完善资料页
                    mBfg.pushFragment(new PerfectInfoFrag(), PF_Back);
                } else {
                    LocalCenter.send(Login_Success, 0, 0);
                    if (mBfg != null) mBfg.notifyActivity(LoginAs, 0, "");
                }
                break;
        }
        return true;
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        if (mBfg != null) {
            mBfg.hideLoading();
        }
        switch (id) {
            case HttpLogin:
                isLogining = false;
                showShortToast(TextUtils.isEmpty(errMsg) ? "登录失败，请重试" : errMsg);
                if (isNormalLogin) {
                    return;
                }
//            if ((errCode == ELoginError || errCode == ENoLogin)){
                if (mBfg != null) mBfg._log("自动登录，账号密码错误，跳转到登录页面");
//                LocalCenter.send(HomeFrag.IA_AutoLogin, 0, 0);
//            }
                break;
            case HttpLogout:
                clearUser();
                break;
        }
    }

    @Override
    public void onHttpFinish(int id, boolean bOK) {
    }

}
