package com.k12app.frag.acc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;

/**
 * 分享
 */
public class ShareFrag extends TitleFrag {
    public static final int FID = FidAll.AboutusFrag;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cnt, Bundle saved) {
        if (mRoot == null) {
            mRoot = inf.inflate(R.layout.frg_acc_share, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {

    }

    private void initData() {

    }
}
