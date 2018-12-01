package com.k12app.frag.acc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.common.IAutoParams;
import com.k12app.core.FidAll;
import com.k12app.core.TitleFrag;

import z.frame.ICommon;

/**
 * 关于我们
 */
public class AboutusFrag extends TitleFrag {
    public static final int FID = FidAll.AboutusFrag;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cnt, Bundle saved) {
        if (mRoot == null) {
            mRoot = inf.inflate(R.layout.frg_aboutus, null);
            setName("关于我们页面");

            setTitle(null, "关于我们", null);
            ICommon.Util.setText(mRoot, R.id.mTvVer, "当前版本 " + IAutoParams.Sec.loadString(IAutoParams.kVerS));
            ICommon.Util.setText(mRoot, R.id.mTvQQ, "用户反馈QQ群：549757172");
//            Util.setText(mRoot, R.id.mTvQQ, "用户反馈QQ群：" + IAutoParams.Sec.loadString(IAutoParams.kQQ));
        }
        return mRoot;
    }
}