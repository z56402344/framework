package com.k12app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.k12app.R;
import com.k12app.frag.acc.AboutusFrag;
import com.k12app.frag.acc.CallCenterFrag;
import com.k12app.frag.acc.ImageFrag;
import com.k12app.frag.acc.MsgSwitchFrag;
import com.k12app.frag.acc.ResetPwdFrag;
import com.k12app.frag.acc.SettingFrag;
import com.k12app.frag.login.LoginFrag;
import com.k12app.frag.login.PerfectInfoFrag;
import com.k12app.frag.login.RegisterFrag;
import com.k12app.frag.login.RegisterProtocolFrag;
import com.k12app.frag.main.GoodsDetailFrag;
import com.k12app.frag.main.MsgFrag;
import com.k12app.frag.main.WebViewFrag;

import z.frame.BaseAct;
import z.frame.BaseFragment;


/**
 * SecondAct
 */
public class SecondAct extends BaseAct {
    private static final int ID_RECORD_FAIL = ID_Loading + 1000; // 显示录音失败提示界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置容器
        setContentView(R.layout.act_cnt);
        mCntId = R.id.scr_cnt;
        if (savedInstanceState == null) {// && !checkRongRun(getIntent())) {
            intoModule(getIntent().getExtras(), 0);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        if (!checkRongRun(getIntent())){
        intoModule(intent.getExtras(), PF_Back);
//        }
    }

    // 外部跳转到本activity的frag
    private void intoModule(Bundle args, int flags) {
        if (args == null) {
            finish();
            return;
        }
        BaseFragment bf = null;
        FragmentManager fm = getSupportFragmentManager();
        switch (args.getInt(kFID, LoginFrag.FID)) {
        case LoginFrag.FID: // 登陆
            bf = new LoginFrag();
            break;
        case RegisterFrag.FID: // 注册
            bf = new RegisterFrag();
            break;
        case PerfectInfoFrag.FID: // 完善资料
            bf = new PerfectInfoFrag();
            break;
        case CallCenterFrag.FID: // 客服中心
            bf = new CallCenterFrag();
            break;
        case WebViewFrag.FID:
            bf = new WebViewFrag();
            break;
        case MsgFrag.FID:
            bf = new MsgFrag();
            break;
        case SettingFrag.FID:
            bf = new SettingFrag();
            break;
        case GoodsDetailFrag.FID:
            bf = new GoodsDetailFrag();
            break;
        case RegisterProtocolFrag.FID:
            bf = new RegisterProtocolFrag();
            break;
        case ResetPwdFrag.FID:
            bf = new ResetPwdFrag();
            break;
        case AboutusFrag.FID:
            bf = new AboutusFrag();
            break;
        case MsgSwitchFrag.FID:
            bf = new MsgSwitchFrag();
            break;
        case ImageFrag.FID:
            bf = new ImageFrag();
            break;
        }
        bf.setArguments(args);
        pushFragment(bf, flags);
        fm.executePendingTransactions();
    }

}