package com.k12app.frag.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.net.IKey;

/**
 * Tab-2
 */
public class TabSubscribeFrag extends TitleFrag implements IKey,IAct {

    public static final int FID = FidAll.TabSubscribe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_tab_subscribe, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {

    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                super.onClick(v);
                break;
        }
    }

    //通过 commitAction()或者广播broadcast()方法发出,响应对应回调的方法
    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id){
            default:
                super.handleAction(id, arg, extra);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //双击tab刷新逻辑
    @Override
    public void clickRefresh() {
        super.clickRefresh();

    }

}
