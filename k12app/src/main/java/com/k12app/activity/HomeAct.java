package com.k12app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.k12app.R;
import com.k12app.core.IAct;
import com.k12app.core.UserLogin;
import com.k12app.db.dao.IUser;
import com.k12app.frag.login.GuideFrag;
import com.k12app.frag.login.LoginFrag;
import com.k12app.frag.main.HomeFrag;
import com.k12app.frag.main.SplashFrag;
import com.k12app.frag.main.TabHomeFrag;

import z.frame.BaseAct;

// 第1层activity 单例
// 管理从闪屏->导航->登录->注册->主页一连串逻辑
// 从主页之后的页面都弹出到第2层activity中
public class HomeAct extends BaseAct implements IAct {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置容器
        setContentView(R.layout.act_cnt);
//        PushManager.getInstance().initialize(this.getApplicationContext());
        mCntId = R.id.scr_cnt;
        registerLocal(HomeFrag.IA_AutoLogin);
        onEnterCreate(savedInstanceState);
    }

    protected void onEnterCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // 显示闪屏页面
            switch (getIntent().getIntExtra(kFID, SplashFrag.FID)) {
                case SplashFrag.FID: // 需要支持从其他入口进来
                    pushFragment(new SplashFrag(), 0);
                    break;
                case GuideFrag.FID: // 导航页面
                case SplashFrag.FExit: // splash页面退出
                    onActMsg(SplashFrag.FExit, null, 0, null);
                    break;
                case HomeFrag.FID:
//				if (UserLogin.checkAutoLogin()) {
//					// 有用户 进入主页
//					gotoHomeFrag();
//				} else {
//					// 没用户 相当于重进应用
//					pushFragment(new SplashFrag(),0);
//				}
                    break;
                case TabHomeFrag.FID_PUT_ON_TOP:
                    gotoHomeFrag();
                    break;
            }
        }
    }

    @Override
    public int onActMsg(int id, Object sender, int arg, Object extra) {
        switch (id) {
            case LoginAs:
                // 执行登录
                gotoHomeFrag();
                break;
            case SplashFrag.FExit: { // 闪屏页面退出

                String token = IUser.Dao.getToken();
                if (TextUtils.isEmpty(token)) {//未注册未登录
                    pushFragment(new GuideFrag(), 0);
                } else {
                    if (IUser.Dao.checkAutoLogin()) {//自动登录
//                        UserLogin.autoLogin();
                        onActMsg(LoginAs, null, 0, null);
                    } else {
                        pushFragment(new GuideFrag(), 0);
                    }
                }

                break;
            }
            default:
                super.onActMsg(id, sender, arg, extra);
                break;
        }
        return 1;
    }

    public void gotoHomeFrag() {
        if (mIsResume) {
            // 弹出所有界面
            popAll();
            // 进入主界面
            HomeFrag hf = new HomeFrag();
            hf.setArguments(getIntent().getExtras());
            pushFragment(hf, 0);
        } else {
            Intent it = new Intent(this, HomeAct.class);
            it.putExtra(kFID, HomeFrag.FID);
            startActivity(it);
        }
    }

    private void onLogout() {
        // 退出登录 跳到导航页面
        popAll();
//        clearAutoLogin();
        UserLogin.clearUser();
        pushFragment(new LoginFrag(), 0);
    }

    private void onLogin() {
        popAll();
        UserLogin.clearUser();
        pushFragment(new GuideFrag(), 0);
        pushFragment(new LoginFrag(), PF_Back);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fid = intent.getIntExtra(kFID, 0);
        _log("onNewIntent >>> fid=" + fid);
        if (fid == GuideFrag.FID) {
            onLogout();
        } else if (fid == LoginFrag.FID) {
            onLogin();
        } else if (fid == HomeFrag.FID) {
            FragmentManager fm = getSupportFragmentManager();
            HomeFrag hf = (HomeFrag) fm.findFragmentByTag(HomeFrag.class.getName());
            if (hf != null) {
                hf.handleArgs(intent.getExtras());
                return;
            }
            // 弹出所有界面
            popAll();
            // 进入主界面
            hf = new HomeFrag();
            hf.setArguments(intent.getExtras());
            pushFragment(hf, 0);
        }
    }

    public static boolean fixOpen(Context ctx) {
        // 当前没有主界面 将主界面弹出来
        if (app.am.findActivity(HomeAct.class) == null) {
            Intent it = new Intent(ctx, HomeAct.class);
            it.putExtra(kFID, HomeFrag.FID);
            ctx.startActivity(it);
            return true;
        }
//		if (ActivityManager.getScreenManager().findActivity(HomeActivity.class) == null) {
//			startActivity(HomeActivity.class);
//		}
        return false;
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
        case HomeFrag.IA_AutoLogin:
            Intent it = new Intent(this, HomeAct.class);
            it.putExtra(HomeAct.kFID, LoginFrag.FID);
            startActivity(it);
            pop(false);
            break;
        }
        super.handleAction(id, arg, extra);
    }
}
