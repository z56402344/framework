package com.k12app.frag.acc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.k12app.R;
import com.k12app.common.ContantValue;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.service.ICmd;
import com.k12lib.afast.utils.NetUtil;

import z.db.ShareDB;
import z.frame.DelayAction;

/**
 * 消息开关页面
 */
public class MsgSwitchFrag extends TitleFrag implements ICmd,HttpItem.IOKErrLis, IAct {

    public static final int FID = FidAll.MsgSwitchFrag;
    public static final int Http_Switch = FID + 1;

    private DelayAction mDelayAct = new DelayAction();
    private ImageView[] mIvButtons = new ImageView[4];
    private boolean[] mIvButtonCheckeds = new boolean[4];

    private ShareDB.Sec mSec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_my_msgswitch, null);
            setTitleText("消息开关");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mIvButtons[0] = (ImageView) findViewById(R.id.IvSubscribe);
        mIvButtons[1] = (ImageView) findViewById(R.id.IvPutTogether);
        mIvButtons[2] = (ImageView) findViewById(R.id.IvMsg);
        mIvButtons[3] = (ImageView) findViewById(R.id.IvMoney);
    }

    private void initData() {
        updateUI();
    }

    private void updateUI() {
        mSec = new ShareDB.Sec(SwiSec);
        mSec.load();
        int isFirst = mSec.getInt(IsFirst, 0);
        //判断总开关的状态
        for (int i = 0; i < mIvButtons.length; i++) {
            //不需要访问网络
//            mIvButtonCheckeds[i] = (mSec.getInt(KEYS[i]) != NOTIFY_OFF);
            if (isFirst == 0) {
                mIvButtonCheckeds[i] = true;
            } else {
                mIvButtonCheckeds[i] = mSec.getBoolean(SwiSec + i);
            }
            refreshData(i);
        }
    }

    public void refreshData(int i) {
        mIvButtons[i].setSelected(mIvButtonCheckeds[i]);
    }

    public void saveData() {
        for (int i = 0; i < mIvButtonCheckeds.length; i++) {
            mSec.put(SwiSec + i, mIvButtonCheckeds[i]);
            mSec.put(IsFirst, 1);
            mSec.save(true);
        }
        httpSwitch();
    }

    public void httpSwitch() {
        if (!NetUtil.checkNet(getActivity())) {
            showShortToast("请检查你的网络状况~");
            return;
        }
//        showLoading(true);
        HttpItem hi = new HttpItem().setListener(this).setId(Http_Switch);
        hi.setUrl(ContantValue.F_DisturbSetting).post(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IvSubscribe:
                mIvButtonCheckeds[0] = !mIvButtonCheckeds[0];
                refreshData(0);
                break;
            case R.id.IvPutTogether:
                mIvButtonCheckeds[1] = !mIvButtonCheckeds[1];
                refreshData(1);
                break;
            case R.id.IvMsg:
                mIvButtonCheckeds[2] = !mIvButtonCheckeds[2];
                refreshData(2);
                break;
            case R.id.IvMoney:
                mIvButtonCheckeds[3] = !mIvButtonCheckeds[3];
                refreshData(3);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        hideLoading();
    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        hideLoading();
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveData();
    }
}
