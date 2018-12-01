package com.k12app.pay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.frag.login.ForgetPwdFrag;
import com.k12app.view.SecurityPasswordEditText;

import z.frame.BaseFragment;

import static com.k12app.core.IAct.kType;


/**
 * 输入支付密码界面
 */
public class InputPayPwdFrag extends BaseFragment {

    public static final int FID = FidAll.InputPayPwd;
    public static final int IA_PayPWD = FID+1;

    private SecurityPasswordEditText mEtPwd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_pay_inputpwd, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mEtPwd = (SecurityPasswordEditText) findViewById(R.id.mEtPwd);
    }

    private void initData() {
        registerLocal(InputPayPwdFrag.IA_PayPWD);
        mEtPwd.setBaseFragment(this);
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id){
            case IA_PayPWD:
                pop(true);
                break;
            default:
                super.handleAction(id, arg, extra);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mIvBack:
                pop(true);
                break;
            case R.id.mTvPwd:
                new Builder(this,new ForgetPwdFrag()).with(kType,1).show();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showImm(false,null);

    }
}
