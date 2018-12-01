package com.k12app.frag.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.activity.SecondAct;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.db.dao.IUser;
import com.k12app.frag.main.GoodsDetailFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.utils.EditListener;
import com.k12app.utils.RegUtil;
import com.k12app.view.MyUtil;
import com.k12lib.afast.utils.NetUtil;

import z.frame.BaseFragment;
import z.frame.DelayAction;

import static com.k12app.net.IKey.RES;


/**
 * 注册页面
 */
public class RegisterFrag extends TitleFrag implements HttpItem.IOKErrLis, IAct {

    public static final int FID = FidAll.RegFragFid;
    private static final int Http_SendCode = FID + 1;
    private static final int Http_Reg = FID + 2;

    private TextView mTvAgreement, mTvGetVer;

    private EditListener mPhoneL = new EditListener();
    private EditListener mVerL = new EditListener();
    private EditListener mPwdL = new EditListener();
    private EditListener mCodeL = new EditListener();
    private CountDownTimer mCountDownTimer; // 计时器

    private long MAX_TIME = 1000 * 60;
    private static final long TAKT_TIME = 100;
    private boolean isGetCodeing = false;
    private String mPhoneNum; // 手机号
    private String mPwd;//密码
    private DelayAction mDelayAct = new DelayAction();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_reg, null);
            setTitleText("注册");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mTvAgreement = (TextView) findViewById(R.id.mTvAgreement);
        mTvAgreement.setOnClickListener(this);
        mTvGetVer = (TextView) findViewById(R.id.mTvGetVer);
    }

    private void initData() {
        mPhoneL.init(mRoot, R.id.mEtPhone, R.id.iv_nickName_x);
        mVerL.init(mRoot, R.id.mEtVer, R.id.iv_ver_x);
        mPwdL.init(mRoot, R.id.mEtPwd, R.id.iv_pwd_x);
        mCodeL.init(mRoot, R.id.mEtInvitationCode, R.id.iv_invitation_code_x);
        initTimer();
        updateUI();
    }

    private void updateUI() {
        if (TextUtils.isEmpty(mTvAgreement.getText().toString())) {
            String str = "注册即代表同意 k12 服务协议 和 隐私条款";
            SpannableString spanString = new SpannableString(str);
            try {
                ForegroundColorSpan span1 = new ForegroundColorSpan(mTvAgreement.getResources().getColor(R.color.gray_e0e0e0));
                ForegroundColorSpan span3 = new ForegroundColorSpan(mTvAgreement.getResources().getColor(R.color.gray_e0e0e0));
                ForegroundColorSpan span2 = new ForegroundColorSpan(mTvAgreement.getResources().getColor(R.color.yellow_FF6F0F));
                spanString.setSpan(span1, 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(span2, 12, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(span3, 17, 18, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(span2, 18, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mTvAgreement.append(spanString);
        }
    }

    @Override
    public void onClick(View v) {
        if (mDelayAct.invalid()) return;
        switch (v.getId()) {
        case R.id.mTvGetVer:
            getCheckPhoneNum();
            break;
        case R.id.btn_reg:
            httpReg();
            break;
        case R.id.mTvAgreement:
//            new Builder(this, new RegisterProtocolFrag()).show();
            new BaseFragment.Builder(getContext(), SecondAct.class, GoodsDetailFrag.FID)
                    .with(GoodsDetailFrag.kUrl, ContantValue.F_AppUserPrivacy)
                    .with(GoodsDetailFrag.kTitle, "服务协议/隐私条款")
                    .show();
            break;
        default:
            super.onClick(v);
            break;
        }
    }

    private void httpReg() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        mPhoneNum = mPhoneL.getTrimString();
        String vercode = mVerL.getTrimString();
        mPwd = mPwdL.getTrimString();

        if (TextUtils.isEmpty(mPhoneNum)) {
            showShortToast("手机号不能为空");
            return;
        }
        boolean isMobileNum = RegUtil.isMobileNum(mPhoneNum);
        if (!isMobileNum) {
            showShortToast("输入的手机号不正确");
            return;
        }
        if (TextUtils.isEmpty(vercode)) {
            showShortToast("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(mPwd)) {
            showShortToast("密码不能为空");
            return;
        }
        String code = mCodeL.getTrimString();
        HttpItem hi = new HttpItem();
        hi.setUrl(ContantValue.F_Reg).setId(Http_Reg);
        hi.put("mobile", mPhoneNum)
                .put("password", mPwd)
                .put("check_code", vercode);
        if (!TextUtils.isEmpty(code)){
            hi.put("invite_code",code);
        }
        hi.setListener(this).post(this);
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
            if (mCountDownTimer != null){
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

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
        case Http_SendCode:
            String msg = TextUtils.isEmpty(errMsg) ? "获取验证码失败,请重试" : errMsg;
            showShortToast(msg);
            initTimer();
            break;
        case Http_Reg:
            showShortToast(TextUtils.isEmpty(errMsg)?"注册失败,请重试":errMsg);
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
        case Http_Reg:
            _log("注册成功 >>> ");
            String token = resp.getString(RES,"token");
            // 设置登录状态
            IUser.Dao.updateToken(token);
            IUser.Dao.updateAccAndPwd(mPhoneNum,mPwd);
            pop(false);
            new Builder(this, new PerfectInfoFrag()).show();
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
