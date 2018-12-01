package com.k12app.frag.acc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.k12app.R;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.view.CustomTextView;
import com.k12lib.afast.utils.NetUtil;

import z.frame.DelayAction;

/**
 * 反馈页面
 */
public class CallCenterFrag extends TitleFrag implements HttpItem.IOKErrLis, TextWatcher {

    public static final int FID = FidAll.CallCenter;
    private static final int FeedBack = FID + 1;

    private EditText mEtFeedBack;
    private TextView mTvNum;
    private DelayAction mDelayAct = new DelayAction();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_callcenter, null);
            setTitleText("投诉与建议");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mEtFeedBack = (EditText) findViewById(R.id.mEtFeedBack);
        mTvNum = (TextView) findViewById(R.id.mTvNum);
    }

    private void initData() {
        CustomTextView tv_right = (CustomTextView) setRightText("提交");
        tv_right.setTextColor(tv_right.getResources().getColor(R.color.yellow_FF6F0F));
        mEtFeedBack.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mDelayAct.invalid()) return;
        switch (v.getId()) {
            case R.id.tv_call:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0370-2082168"));
                startActivityEx(intent);
                break;
            case R.id.right:
                feedBack();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void feedBack() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast(getResources().getString(R.string.net_error));
            return;
        }
        showLoading(true);
        HttpItem hi = new HttpItem();
        hi.setUrl(ContantValue.F_FeedBack);
        hi.setId(FeedBack).put("content", mEtFeedBack.getText().toString().trim()).setListener(this).post(this);

    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
        switch (id) {
            case FeedBack:
                showShortToast(TextUtils.isEmpty(errMsg) ? "提交反馈失败" : errMsg);
                break;
        }
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        showShortToast(TextUtils.isEmpty(msg) ? "反馈成功,请耐心等待客服给您回复" : msg);
        pop(true);
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mTvNum.setText(mEtFeedBack.getText().toString().length() + "/500");
    }
}
