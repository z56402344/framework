package com.k12app.frag.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.k12app.R;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.utils.EditListener;
import com.k12app.utils.RegUtil;
import com.k12app.view.CustomEditText;
import com.k12app.view.CustomTextView;
import com.k12app.view.MyUtil;
import com.k12lib.afast.utils.NetUtil;

import z.frame.DelayAction;


/**
 * 忘记登录密码/找回交易密码页面
 */
public class ForgetPwdFrag extends TitleFrag implements HttpItem.IOKErrLis, IAct {

    public static final int FID = FidAll.ForgetPwdFrag;
    private static final int Http_SendCode = FID + 1;
    private static final int Http_SetPwd = FID + 2;

    private CustomTextView mTvGetVer;
    private CustomEditText mEtPwd;
    private RelativeLayout mRlPhone;

    private EditListener mPhoneL = new EditListener();
    private EditListener mVerL = new EditListener();
    private EditListener mPwdL = new EditListener();
    private CountDownTimer mCountDownTimer; // 计时器

    private long MAX_TIME = 1000 * 60;
    private static final long TAKT_TIME = 100;
    private boolean isGetCodeing = false;
    private String mPhoneNum; // 手机号
    private DelayAction mDelayAct = new DelayAction();
    private int mType = 0;//0==忘记登录页面，1==找回交易密码页面

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_forgetpwd, null);

            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mTvGetVer = (CustomTextView) findViewById(R.id.mTvGetVer);
        mRlPhone = (RelativeLayout) findViewById(R.id.mRlPhone);
        mEtPwd = (CustomEditText) findViewById(R.id.mEtPwd);

    }

    private void initData() {
        Bundle arg = getArguments();
        if (arg == null) return;
        mType = arg.getInt(kType, 0);
        if (mType == 0){
            setTitleText("忘记密码");
        }else{
            setTitleText("设置交易密码");
        }
        mPhoneL.init(mRoot, R.id.mEtPhone, R.id.iv_nickName_x);
        mVerL.init(mRoot, R.id.mEtVer, R.id.iv_ver_x);
        mPwdL.init(mRoot, R.id.mEtPwd, R.id.iv_pwd_x);
        mEtPwd.setHint(mType == 0 ? "请输入新登录密码" : "请输入新交易密码");
        initTimer();
    }


    @Override
    public void onClick(View v) {
        if (mDelayAct.invalid()) return;
        switch (v.getId()) {
            case R.id.mTvGetVer:
                getCheckPhoneNum();
                break;
            case R.id.btn_pwd:
                httpPwd();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void httpPwd() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }

        if (mType == 0) {
            mPhoneNum = mPhoneL.getTrimString();
            if (TextUtils.isEmpty(mPhoneNum)) {
                showShortToast("手机号不能为空");
                return;
            }
            boolean isMobileNum = RegUtil.isMobileNum(mPhoneNum);
            if (!isMobileNum) {
                showShortToast("输入的手机号不正确");
                return;
            }
        }

        String ver = mVerL.getTrimString();
        String pwd = mPwdL.getTrimString();

        if (TextUtils.isEmpty(ver)) {
            showShortToast("验证码不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            showShortToast("密码不能为空");
            return;
        }

        HttpItem hi = new HttpItem();
        hi.setId(Http_SetPwd).setListener(this);
        if (mType == 0) {
            // 忘记登录密码
            hi.put("mobile", mPhoneNum).put("password", pwd).put("checkcode", ver).setUrl(ContantValue.F_GetPassword);
        } else {
            //找回交易密码
            hi.put("password", pwd).put("checkcode", ver).setUrl(ContantValue.F_GetTradePwd);
        }
        hi.post(this);
        showLoading(true);
    }

    /**
     * 重新获取验证码
     */
    private void getCheckPhoneNum() {
        mPhoneNum = mPhoneL.getTrimString();
        if (TextUtils.isEmpty(mPhoneNum)) {
            showShortToast("手机号不能为空");
            return;
        }
        boolean isMobileNum = RegUtil.isMobileNum(mPhoneNum);
        if (!isMobileNum) {
            showShortToast("输入的手机号不正确");
            return;
        }

        showImm(false, null);
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        if (!isGetCodeing) {
            isGetCodeing = true;
            showLoading(true);
            if (mCountDownTimer != null) {
                mCountDownTimer.start();
            }
            // 1.发送验证
            // 2.验证码输入错误点击重新发送验证码
            HttpItem hi = new HttpItem();
            hi.setUrl(ContantValue.F_RegSms);
            hi.setId(Http_SendCode).put("mobile", mPhoneNum).setListener(this).post(this);

        }
    }

    //验证码倒计时的初始化逻辑
    private void initTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(MAX_TIME, TAKT_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTvGetVer.setText(millisUntilFinished / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    // 倒计时结束
                    mTvGetVer.setEnabled(true);
                    mTvGetVer.setText(R.string.refresh_checkNum);
                    isGetCodeing = false;
                }
            };
        } else {
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
            case Http_SendCode:
                String msg = TextUtils.isEmpty(errMsg) ? "获取验证码失败,请重试" : errMsg;
                showShortToast(msg);
                initTimer();
                break;
            case Http_SetPwd:
                showShortToast(TextUtils.isEmpty(errMsg) ? "找回密码失败,请重试" : errMsg);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        switch (resp.id) {
            case Http_SendCode:
                _log("获取msgcode成功 >>> ");
                MyUtil.showSquareToast("验证码已发送");
                break;
            case Http_SetPwd:
                _log("重设密码成功 >>> ");
                showShortToast(TextUtils.isEmpty(msg) ? mType == 0 ?"重设密码成功":"设置交易密码成功" : msg);
                pop(false);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initTimer();
    }
}
