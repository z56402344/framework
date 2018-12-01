package com.k12app.frag.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;


/**
 * 登陆/注册页面
 */
public class RegisterProtocolFrag extends TitleFrag {

    public static final int FID = FidAll.RegisterProtocolFrag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_register_protocol, null);
            setTitleText("注册协议");
        }
        return mRoot;
    }

}
