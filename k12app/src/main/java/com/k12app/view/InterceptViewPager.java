package com.k12app.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 拦截viewPage手势，避免手势冲突
 */
public class InterceptViewPager extends ViewPager {

    private float mDisX;//水平方向手势滑动距离
    public static final float VALID_GRSTURE_DISTANCE = 50f;//

    public InterceptViewPager(Context context) {
        super(context);
    }

    public InterceptViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDisX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(ev.getRawX() - mDisX);
                if (dx > VALID_GRSTURE_DISTANCE) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
