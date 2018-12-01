package com.k12app.frag.acc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.core.TitleFrag;
import com.k12app.utils.ImageLoderUtil;
import com.k12lib.afast.view.RecycleImageView;

import z.image.universal_image_loader.core.DisplayImageOptions;

/**
 * 点击图片放大页面
 */
public class ImageFrag extends TitleFrag implements IAct{
    public static final int FID = FidAll.ImageFragFID;

    private RecycleImageView mIvPic;
    private DisplayImageOptions mOpt;

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cnt, Bundle saved) {
        if (mRoot == null) {
            mRoot = inf.inflate(R.layout.frg_image, null);
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mIvPic = (RecycleImageView) findViewById(R.id.mIvPic);
    }

    private void initData() {
        Bundle args = getArguments();
        if (args == null)return;
        String url = args.getString(DATA);
        mOpt = ImageLoderUtil.setImageRoundLogder(R.mipmap.bg_default, app.px(5));
        mIvPic.init(url,mOpt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.mLlBack:
            pop(true);
            break;
        }
        super.onClick(v);
    }
}
