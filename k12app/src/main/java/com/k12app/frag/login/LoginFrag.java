package com.k12app.frag.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.k12app.R;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.IUmEvt;
import com.k12app.core.TitleFrag;
import com.k12app.core.UserLogin;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.utils.EditListener;
import com.k12app.view.CustomTextView;
import com.k12app.view.MyUtil;
import com.k12lib.afast.utils.NetUtil;

import z.frame.DelayAction;
import z.frame.UmBuilder;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

/**
 * 登陆逻辑页面
 */
public class LoginFrag extends TitleFrag implements HttpItem.IOKErrLis, IAct {

    private static final String EvtID = IUmEvt.Login;
    public static final int FID = FidAll.LoginFragFid;
    public static final int Http_SendCode = FID + 1;
    public static final int HTTP_LOGIN = FID + 2;
    public static final String AC = "ac";//账号
    public static final String PW = "pw";//密码
    public static final String PW_Desc = "pwDesc";//密码提示描述

    // 防止点击太频繁
    private DelayAction mDelay = new DelayAction();
    private EditText mEtNickName;
    private EditText mEtPwd;
    private String mPassWord;
    private String mAccount;
    private CustomTextView mTvTabPwd,mTvTabCode,mTvGetVer;
    private View mLinePwd,mLineCode;
    private ImageView mIvPwd;

    private EditListener mNameEditL = new EditListener();
    private EditListener mPwdEditL = new EditListener();

    private UserLogin mUserLogin;
    private View mDebugView;

    private ListView mLvAccountHistory;
    private String mPhoneNum;
    private CountDownTimer mCountDownTimer; // 计时器
    private long MAX_TIME = 1000 * 60;
    private static final long TAKT_TIME = 100;

    private int mLoginType = 0;//0=密码登录，1==动态码登录
    private boolean isGetCodeing = false;//是否正在发送验证码过程中


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_login, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        setTitleText("登录");
        mNameEditL.init(mRoot, R.id.editTxt_nickName, R.id.iv_nickName_x);
        mPwdEditL.init(mRoot, R.id.mEtPwd, R.id.iv_password_x);
        mTvGetVer = (CustomTextView) findViewById(R.id.mTvGetVer);
        mEtNickName = (EditText) findViewById(R.id.editTxt_nickName);
        mEtPwd = (EditText) findViewById(R.id.mEtPwd);
        mEtPwd.setImeOptions(EditorInfo.IME_ACTION_SEND);
//        mEtPwd.setOnEditorActionListener(this);
        mTvTabPwd = (CustomTextView) findViewById(R.id.mTvTabPwd);
        mTvTabCode = (CustomTextView) findViewById(R.id.mTvTabCode);
        mLinePwd = findViewById(R.id.mLinePwd);
        mLineCode = findViewById(R.id.mLineCode);
        mIvPwd = (ImageView) findViewById(R.id.mIvPwd);

    }

    private void initData() {
        mUserLogin = new UserLogin(mRoot.getContext());
        Bundle args = getArguments();
        if (args != null) {
            String ac = args.getString(AC);
            String pw = args.getString(PW);
            String pwDesc = args.getString(PW_Desc);
            if (!TextUtils.isEmpty(ac)) {
                mEtNickName.setText(ac);
            }
            if (!TextUtils.isEmpty(pwDesc)) {
                mEtPwd.setHint(pwDesc);
            }
            mEtPwd.setText(!TextUtils.isEmpty(pw) ? pw : "");
        }
        switchView(0);
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) return;
        switch (v.getId()) {
            case R.id.left: // 默认返回上个界面
                UmBuilder.reportSimple(EvtID, "返回");
                pop(true);
                break;
            case R.id.rl_pwd:
                UmBuilder.reportSimple(EvtID, "切换到密码登录");
                switchView(0);
                break;
            case R.id.rl_code:
                UmBuilder.reportSimple(EvtID, "切换到动态码登录");
                switchView(1);
                break;
            case R.id.btn_login:
                UmBuilder.reportSimple(EvtID, "登录");
                startLogin();
                break;
            case R.id.mTvGetVer:
                UmBuilder.reportSimple(EvtID, "获取动态码");
                httpCode();
                break;
            case R.id.tv_register:
                UmBuilder.reportSimple(EvtID, "注册");
                new Builder(this, new RegisterFrag()).show();
                break;
            case R.id.tv_resetpwd:
                UmBuilder.reportSimple(EvtID, "忘记密码");
                new Builder(this, new ForgetPwdFrag()).show();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    //验证码倒计时的初始化逻辑
    private void initTimer() {
        if (mCountDownTimer == null){
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
        }else{
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }
    }

    public void switchView(int type){
        mLoginType = type;
        if (type == 0){
            mTvTabPwd.setSelected(true);
            mTvTabCode.setSelected(false);
            mLinePwd.setBackgroundResource(R.color.yellow_FF6F0F);
            mLineCode.setBackgroundResource(R.color.gray_e0e0e0);
            mTvGetVer.setVisibility(View.GONE);
            mEtPwd.setHint(R.string.inputpwd);
            mEtPwd.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            mIvPwd.setImageResource(R.mipmap.ic_login_pwd);
        }else{
            initTimer();
            mTvTabPwd.setSelected(false);
            mTvTabCode.setSelected(true);
            mLinePwd.setBackgroundResource(R.color.gray_e0e0e0);
            mLineCode.setBackgroundResource(R.color.yellow_FF6F0F);
            mTvGetVer.setVisibility(View.VISIBLE);
            mEtPwd.setHint(R.string.inputcode);
            mEtPwd.setInputType(TYPE_CLASS_TEXT);
            mIvPwd.setImageResource(R.mipmap.ic_login_code);
        }

    }

    //获取登录动态验证码
    private void httpCode() {
        mPhoneNum = mEtNickName.getText().toString().trim();
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        if (TextUtils.isEmpty(mPhoneNum)) {
            showShortToast("手机号不能为空");
            return;
        }
//        boolean isMobileNum = RegUtil.isMobileNum(mPhoneNum);
//        if (!isMobileNum) {
//            showShortToast("输入的手机号不正确");
//            return;
//        }
        showImm(false, null);
        if (!isGetCodeing) {
            isGetCodeing = true;
            if (mCountDownTimer != null){
                mCountDownTimer.start();
            }
            showLoading(true);
            HttpItem hi = new HttpItem();
            hi.setUrl(ContantValue.F_RegSms);
            hi.setId(Http_SendCode).put("mobile", mPhoneNum).setListener(this).post(this);
        }

    }

    private void startLogin() {
        mAccount = mEtNickName.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            showShortToast("请输入正确的手机号");
            return;
        }

        mPassWord = mEtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(mPassWord)) {
            showShortToast(mLoginType ==0?R.string.inputpwd:R.string.inputcode);
            return;
        }

        if (mRoot != null && !NetUtil.checkNet(mRoot.getContext())) {
            showShortToast("网络连接失败，请稍后再试");
            return;
        }

        showLoading(true);
        showImm(false, null);
        mUserLogin.isNormalLogin = true;
        mUserLogin.setBaseFragment(this);
        mUserLogin.setLoginType(mLoginType).doLogin(mAccount, mPassWord);

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
        default:
            break;
        }
        return false;
    }

//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        //当actionId == XX_SEND 或者 XX_DONE时都触发
//        //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
//        //注意，这时一定要判断event != null。因为在某些输入法上会返回null。
//        if (actionId == EditorInfo.IME_ACTION_SEND
//                || actionId == EditorInfo.IME_ACTION_DONE
//                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//
//            String editStr = v.getText().toString().trim();
//
//            if (TextUtils.isEmpty(editStr)) {
//                showShortToast("请输入密码");
//            } else {
//                //登录逻辑
//                startLogin();
//            }
//        }
//        return false;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initTimer();
    }
}
