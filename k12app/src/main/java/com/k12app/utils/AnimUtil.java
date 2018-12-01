package com.k12app.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import z.frame.ICommon;

/**
 * Created by duguang on 15-9-29.
 */
public class AnimUtil extends ICommon.Util {

    public static ScaleAnimation ScalAnim(View v, float fromD, float toD) {
        return ScalAnim(v, fromD, toD, 500, false);
    }

    public static ScaleAnimation ScalAnim(View v, float fromD, float toD, int dur, boolean isFillAfter) {
        if (v != null) v.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnim = new ScaleAnimation(fromD, toD, fromD, toD, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(dur);
        scaleAnim.setRepeatCount(0);
        scaleAnim.setFillAfter(isFillAfter);
        if (v == null) return scaleAnim;
        v.startAnimation(scaleAnim);
        return scaleAnim;
    }

    public static TranslateAnimation tranAnim(View v, float fromY, float toY) {
        return tranAnim(v, fromY, toY, 200L, false, 0);
    }

    public static TranslateAnimation tranAnim(View v, float fromY, float toY, long dur, boolean isFillAfter, int count) {
        if (v != null) v.setVisibility(View.VISIBLE);
        return tranAnim(v, 0, 0, fromY, toY, dur, isFillAfter, count);
    }

    public static TranslateAnimation tranAnim(View v, float formX, float toX, float fromY, float toY, long dur, boolean isFillAfter, int count) {
        TranslateAnimation tranAnim = new TranslateAnimation(formX, toX, fromY, toY);
        tranAnim.setDuration(dur);
        tranAnim.setFillAfter(isFillAfter);
        tranAnim.setRepeatCount(count);
        if (v == null) return tranAnim;
        v.startAnimation(tranAnim);
        return tranAnim;
    }

    public static AlphaAnimation alpAnim(View v) {
        return alpAnim(v, false, 0, 1, 0, 400, false);
    }

    public static AlphaAnimation alpAnim(View v, float from, float to) {
        return alpAnim(v, false, from, to, 0, 400, false);
    }

    public static AlphaAnimation alpAnim(View v, float from, float to, int count, int dur) {
        return alpAnim(v, false, from, to, count, dur, false);
    }

    public static AlphaAnimation alpAnim(View v, boolean isFillAfter, float from, float to) {
        return alpAnim(v, isFillAfter, from, to, 0, 400, false);
    }

    public static AlphaAnimation alpAnim(View v, boolean isFillAfter, float from, float to, int count, long dur, boolean isReverse) {
        if (v != null) v.setVisibility(View.VISIBLE);
        AlphaAnimation alpAnim = new AlphaAnimation(from, to);
        alpAnim.setDuration(dur);
        alpAnim.setRepeatCount(count);
        if (isReverse) alpAnim.setRepeatMode(Animation.REVERSE);
        alpAnim.setFillAfter(isFillAfter);
        if (v == null) return alpAnim;
        v.startAnimation(alpAnim);
        return alpAnim;
    }

    public static void rotaAnim(View v) {
        RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(15000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        v.startAnimation(anim);
    }

    /**
     * 点赞动画所调用的方法
     *
     * @param view    需要做动画的view
     * @param startId 点赞之前的资源
     */
    public static void praisedAnimation(final ImageView view, final int startId, final int endId) {
//		view.setImageResource(startId);
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f,
                1.0f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setImageResource(endId);
                ScaleAnimation animation1 = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                animation1.setDuration(267);
                view.startAnimation(animation1);
            }
        });
    }
}
