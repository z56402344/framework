package com.k12app.frag.login;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.k12app.R;
import com.k12app.common.IAutoParams;
import com.k12app.core.IUmEvt;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12lib.afast.view.RecycleImageView;

import java.util.ArrayList;

import z.frame.BaseFragment;
import z.frame.DelayAction;
import z.frame.UmBuilder;

// 导航界面
public class GuideFrag extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, HttpItem.IOKErrLis, IKey, View.OnTouchListener {
    public static final int FID = 1100;
    private static final String EvtID = IUmEvt.Guide;

    private ViewPager mVp;
    private GuideAdapter mGuideAdapter = null;
    private LinearLayout mLlDotGroup;
    private LinearLayout mLlBtn;

    // 防止点击太频繁
    private DelayAction mDelay = new DelayAction();
    private ArrayList<View> mGuides = new ArrayList<View>();
    private ImageView[] mDots = null;
    private int mCur = 0;
    public static final int[] mGuideResArr = {R.mipmap.ic_guide1, R.mipmap.ic_guide2, R.mipmap.ic_guide3};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.frg_guide, null);
            setName("引导页面");
            initView();
            initData();
        }
        return mRoot;
    }

    private void initView() {
        mVp = (ViewPager) findViewById(R.id.mVp);
        mLlDotGroup = (LinearLayout) findViewById(R.id.mLlDotGroup);
        mLlBtn = (LinearLayout) findViewById(R.id.mLlBtn);

    }

    private void initData() {
        addPage();
        mGuideAdapter = new GuideAdapter();
        mVp.setAdapter(mGuideAdapter);
        mVp.setOnPageChangeListener(this);
//        initDots();
        mVp.setCurrentItem(0);
        mGuideAdapter.notifyDataSetChanged();
        mVp.setOnTouchListener(this);
    }

    private void addPage() {
        if (mRoot == null) return;
        int size = mGuideResArr.length;
        for (int i = 0; i < size; ++i) {
            View guide = View.inflate(mRoot.getContext(), R.layout.item_guide_blue, null);
            RecycleImageView rlv = (RecycleImageView) guide.findViewById(R.id.rlv_guide);
            rlv.setImageResource(mGuideResArr[i]);
            mGuides.add(guide);
        }
    }

    @Override
    public void onClick(View v) {
        if (mDelay.invalid()) return;
        switch (v.getId()) {
            case R.id.mBtnLogin:
                UmBuilder.reportSimple(EvtID, "登录");
                new Builder(this, new LoginFrag()).show();
                break;
            case R.id.mBtnReg:
                new Builder(this, new RegisterFrag()).show();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    // 初始化圆点
    private void initDots() {
        int len = mGuides.size();
        if (mLlDotGroup.getChildCount() > 0) {
            mLlDotGroup.removeAllViews();
        }
        float deny = IAutoParams.Sec.loadFloat(IAutoParams.kDensity);
        ImageView mIvDot = null;
        mDots = new ImageView[len];
        for (int i = 0; i < len; ++i) {
            if (mRoot == null) return;
            mIvDot = new ImageView(mRoot.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // 点与点间距
            int marg = (int) (8 * deny);
            lp.leftMargin = marg;
            lp.rightMargin = marg;
            mIvDot.setLayoutParams(lp);

            if (i == 0) {
                mIvDot.setImageResource(R.drawable.guide_dot_blue);
            } else {
                mIvDot.setImageResource(R.drawable.guide_dot_normal);
            }
            mDots[i] = mIvDot;
            mLlDotGroup.addView(mIvDot);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
//        mDots[mDotPos].setImageResource(R.drawable.guide_dot_normal);
//        int dot = R.drawable.guide_dot_blue;
//        if (i == 0){
//            dot = R.drawable.guide_dot_blue;
//        }else if(i == 1){
//            dot = R.drawable.guide_dot_yellow;
//        }else if(i == 2){
//            dot = R.drawable.guide_dot_red;
//        }
//        mDots[i].setImageResource(dot);
        mCur = i;
        if (i == mGuides.size() - 1) {
            mLlBtn.setVisibility(View.VISIBLE);
        } else {
            mLlBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {

    }

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        Log.i("lyy", "resp >>>>> " + resp.toString());
        return false;
    }

    float mDownX = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();

                if (mCur == mGuideResArr.length - 1 && mDownX > moveX && (mDownX - moveX) > 50) {
//                    new Builder(this, new RegisterFrag()).with(kType, RegisterFrag.TYPE_LOGIN).show();
                    new Builder(this, new LoginFrag()).show();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    private class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return (mGuides != null) ? mGuides.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            View mi = (View) o;
            return view == mi;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int pos) {
            View mi = mGuides.get(pos);
            container.addView(mGuides.get(pos));
            return mi;
        }

        @Override
        public void destroyItem(ViewGroup container, int pos, Object o) {
            View mi = (View) o;
            container.removeView(mi);
        }
    }

}
