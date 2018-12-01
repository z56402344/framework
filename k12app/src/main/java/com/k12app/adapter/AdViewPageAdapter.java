package com.k12app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.k12app.R;
import com.k12app.utils.ImageLoderUtil;

import z.frame.BaseFragment;
import z.frame.ICommon;
import z.image.universal_image_loader.core.DisplayImageOptions;

public class AdViewPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, Runnable, ICommon {
    private int mListSize;
    private IProxy mProxy;
    private Context mCtx;
    private BaseFragment mBf;
    private ArrayList<View> mViewsList = new ArrayList<View>();
    private DisplayImageOptions mVpOptions;
    private ViewPager mVp;
    public int mCurrentItem = 0;    //当前轮播页
    private final static int TIME_DELAY = 5000; //自动轮播的开始的延迟时间(单位/秒)
    private boolean mIsShowDot;
    private List<View> mDotViewsList = new ArrayList<>();   //放圆点的View的list
    private LinearLayout mLlDots;

    public AdViewPageAdapter(BaseFragment bf, ViewPager vp, IProxy p) {
        mCtx = bf.getActivity();
        mBf = bf;
        mVp = vp;
        mProxy = p;
        mVp.setOnPageChangeListener(this);
        mVpOptions = ImageLoderUtil.setImage(R.mipmap.def_bg);
    }

    public void setData(int size, boolean isShowDot, LinearLayout ll) {
        mIsShowDot = isShowDot;
        mListSize = size;
        mLlDots = ll;
        notifyDataSetChanged();
        if (isShowDot) {
            initViewpagerUI();
        }
        mVp.setCurrentItem(1);
        startPlay();
    }

    private void startPlay() {
        if (mBf != null) {
            mBf.removeRunnable(this);
            mBf.post(this, TIME_DELAY);
        }
    }

    public void initViewpagerUI() {
        if (mListSize == 0) {
            return;
        }
        mLlDots.removeAllViews();
        mDotViewsList.clear();
        for (int i = 0; i < mListSize; i++) {
            ImageView dotView = new ImageView(mCtx);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(app.px(8), app.px(8));
            params.leftMargin = app.px(4);
            params.rightMargin = app.px(4);
            mLlDots.addView(dotView, params);
            mDotViewsList.add(dotView);
        }
        mVp.setCurrentItem(1);
        changeDots(0);
        mLlDots.setVisibility(mIsShowDot ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getCount() {
        if (mListSize <= 1) {
            return mListSize;
        } else {
            return mListSize + 2;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    public int getSelectItem() {
        return fixIndex(mCurrentItem);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        mViewsList.add(view);
        mProxy.destoryView(position, view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (mViewsList.size() > 0) {
            view = mViewsList.get(0);
            mViewsList.remove(0);
        } else {
            view = mProxy.createView();
        }
        if (position == 0) {
            position = mListSize - 1;
        } else if (position > mListSize) {
            position = 0;
        } else {
            position -= 1;
        }
        if (position >= 0 && position < mListSize) {
            mProxy.updateView(view, position, mVpOptions);
        }
        container.addView(view);
        return view;
    }

    private void changeDots(int pos) {
        for (int i = 0; i < mDotViewsList.size(); i++) {
            if (i == pos) {
                (mDotViewsList.get(pos)).setBackgroundResource(R.drawable.icon_dot_white);
            } else {
                (mDotViewsList.get(i)).setBackgroundResource(R.drawable.icon_dot_white2);
            }
        }
    }




    @Override
    public void run() {
        //执行轮播图切换任务
        if (mListSize < 2) return;
        mCurrentItem = (mCurrentItem + 1) % (mListSize + 2);
        mCurrentItem = fixIndex(mCurrentItem);
        mVp.setCurrentItem(mCurrentItem);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        mCurrentItem = fixIndex(i);

        startPlay();
        changeDots(mCurrentItem - 1);
        if (i != mCurrentItem) {
//            mVp.setCurrentItem(mCurrentItem, false);
            return;
        }
    }

    // [1,count] 0->count count+1->1
    public int fixIndex(int idx) {
        if (idx == 0) {
            idx = mListSize;
        } else if (idx > mListSize) {
            idx = 1;
        }
        return idx;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        switch (i) {
            case ViewPager.SCROLL_STATE_DRAGGING:// 手势滑动，空闲中
                break;
            case ViewPager.SCROLL_STATE_SETTLING:// 界面切换中
                mVp.removeCallbacks(this);
                break;
            case ViewPager.SCROLL_STATE_IDLE:// 滑动结束，即切换完毕或者加载完毕
                if (mCurrentItem != mVp.getCurrentItem()) {
                    mVp.setCurrentItem(mCurrentItem, false);
                }
                break;
        }
    }

    public void stop() {
        if (mBf != null) {
            mBf.removeRunnable(this);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface IProxy {
        View createView();

        void updateView(View view, int position, DisplayImageOptions ops);

        void destoryView(int pos, View view);
    }
}
