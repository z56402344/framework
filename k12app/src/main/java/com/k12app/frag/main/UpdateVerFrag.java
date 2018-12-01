package com.k12app.frag.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.k12app.R;
import com.k12app.bean.AppVersionBean;
import com.k12app.common.IAutoParams;
import com.k12app.net.SyncMgr;
import com.k12app.service.ICmd;
import com.k12app.service.LocalService;
import com.k12lib.afast.view.RecycleImageView;

import z.frame.BaseFragment;

//说明：新版本提示
public class UpdateVerFrag extends BaseFragment implements View.OnClickListener {

    public static final int FID = 3200;

    public static final int TVersion = 0x01; // 显示新版本
    public static final int TRegisterSuccess = 0x02; //注册成功弹框

    private int mType = 0;
    private RecycleImageView mIvDialog;

    public static final boolean hasBit(int src, int bit) {
        return (src & bit) == bit;
    }

    public static final int removeBit(int src, int bit) {
        return src & ~bit;
    }

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cnt, Bundle saved) {
        if (mRoot == null) {
            mType = getArguments().getInt("type");
            setName("新版本弹框");
            if (hasBit(mType, TVersion)) {
                initNewVersion(inf);
            } else {
                pop(false);
            }
        }
        return mRoot;
    }

    //新版本提示的Dialog
    private void initNewVersion(LayoutInflater inf) {
        AppVersionBean mVer = SyncMgr.getVer();
        mRoot = inf.inflate(R.layout.frg_new_version, null);
        Util.setText(mRoot, R.id.mTvContent, TextUtils.isEmpty(mVer.version_des) ? "" : mVer.version_des);
        Util.setText(mRoot, R.id.mTvTitle, "新版本" + mVer.version);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnOk:
                Intent in = new Intent(getActivity(), LocalService.class);
                in.putExtra(ICmd.Key, ICmd.DOWN_APK);
                getActivity().startService(in);
                pop(false);
                break;
            case R.id.mBtnCancel:
                removeAndGoNext(TVersion);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            default:
                break;

        }
    }

    // 移除某标志并进入下一个
    private void removeAndGoNext(int bit) {
        pop(false);
        mType = removeBit(mType, bit);
        if (mType != 0) {
            pushFragment(create(mType, this), PF_Back | PF_Add);
        }
    }

    // 检查新版本
    public static int checkNewVersion() {
        AppVersionBean mVer = SyncMgr.getVer();
        if (mVer != null && !TextUtils.isEmpty(mVer.version) && !TextUtils.isEmpty(mVer.version_url)) {
            String curVer = IAutoParams.Sec.loadString(IAutoParams.kVerS);
            if (isVerNew(curVer, mVer.version)) {
                return TVersion;
            }
        }
        return 0;
    }

    public static boolean isVerNew(String currentVersion, String newVersion) {
        if (TextUtils.isEmpty(newVersion) || TextUtils.isEmpty(currentVersion)) return true;
        String[] cur = currentVersion.split("[.]");
        String[] svr = newVersion.split("[.]");
        int len = cur.length > svr.length ? svr.length : cur.length;
        for (int i = 0; i < len; ++i) {
            try {
                int c = Integer.parseInt(cur[i]);
                int n = Integer.parseInt(svr[i]);
                if (n > c) return true;
                if (n < c) return c > 900;//900以上是灰度发布版本号,此策略是为正式版发布时灰度版本也会提示升级
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // 检查并创建界面
    public static void checkAndShow(BaseFragment src) {
        int type = checkNewVersion();
        if (type == 0) return;
        src.pushFragment(create(type, src), PF_Back | PF_Add);
    }


    public static UpdateVerFrag create(int type, BaseFragment src) {
        UpdateVerFrag fg = new UpdateVerFrag();
        Bundle args;
        if (src instanceof UpdateVerFrag) {
            args = src.getArguments();
        } else {
            args = new Bundle();
        }
        args.putInt("type", type);
        fg.setArguments(args);
        return fg;
    }


}
