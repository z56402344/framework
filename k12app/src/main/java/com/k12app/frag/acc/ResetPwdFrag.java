package com.k12app.frag.acc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.utils.EditListener;
import com.k12app.view.CustomEditText;
import com.k12lib.afast.utils.NetUtil;
import com.k12lib.afast.utils.StringUtil;

import z.frame.DelayAction;

// 修改登录密码/修改交易密码
public class ResetPwdFrag extends TitleFrag implements HttpItem.IOKErrLis,IAct,IKey {

    public static final int FID = FidAll.ResetPwdFrag;
    private CustomEditText mEtPwd, mEtNewPwd;

    private EditListener mPwd = new EditListener();
    private EditListener mPwdAgain = new EditListener();
    private String mPassword, mNewPassword; // 用户密码
    // 防止点击太频繁
    private DelayAction mDelay = new DelayAction();
    private int mType = 0;//0==修改登录密码，1==修改交易密码

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_reset_pwd, null);
            setTitleText("修改密码");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mPwd.init(mRoot, R.id.edt_pwd, R.id.iv_pwd_x);
        mPwdAgain.init(mRoot, R.id.edt_new_pwd, R.id.iv_newpwd_x);
        mEtPwd = (CustomEditText) findViewById(R.id.edt_pwd);
        mEtNewPwd = (CustomEditText) findViewById(R.id.edt_new_pwd);
    }

    private void initData() {
        Bundle arg = getArguments();
        if (arg == null)return;
        mType = arg.getInt(kType,0);
        if (mType == 0){
            mEtPwd.setHint("输入旧的登陆密码");
            mEtNewPwd.setHint("输入新的登陆密码");
        }else{
            mEtPwd.setHint("输入旧的交易密码");
            mEtNewPwd.setHint("输入新的交易密码");
        }
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) return;
        switch (v.getId()) {
        case R.id.btn_save:
            // 修改密码
            startNewPassword();
            break;
        default:
            super.onClick(v);
            break;
        }
    }

    //设置新密码请求接口
    private void startNewPassword() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
        mPassword = mPwd.getTrimString();
        mNewPassword = mPwdAgain.getTrimString();

        if (StringUtil.isEmpty(mPassword) || StringUtil.isEmpty(mNewPassword)) {
            showShortToast("输入的密码不能为空");
            return;
        }
//		if (mPassword.length() < 6 || mNewPassword.length() < 6) {
//			showShortToast("请输入至少6位密码");
//			return;
//		}

//        if (!mPassword.equals(mNewPassword)) {
//            showShortToast("两次密码输入不一致");
//            return;
//        }
        showLoading(true);
        HttpItem hi = new HttpItem().setListener(this);
        hi.put("old_password", mPassword).put("new_password", mNewPassword);
        if (mType == 0){
            hi.setUrl(ContantValue.F_SETPWD);
        }else{
            hi.setUrl(ContantValue.F_ModifyTradePwd);
        }
        hi.post(this);
    }

    // 修改密码请求接口的回调
    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        showShortToast(TextUtils.isEmpty(msg)?"修改密码成功":msg);
        pop(true);
        return true;
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        showShortToast(HttpResp.filter(errCode, errMsg, "修改密码失败"));
    }
}
