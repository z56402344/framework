package com.k12app.pay;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public abstract class WeakAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    public static final int FLAG_STRONG_REFERENCE = 0;
    public static final int FLAG_WEAK_REFERENCE = 1;

    public static final int FID = 1000;
    public static final int MSG_FIRST = FID + 1;
    public static final int MSG_SECOND = FID + 2;
    public static final int MSG_THIRD = FID + 3;
    public static final int MSG_FOURTH = FID + 4;
    public static final int MSG_FIFTH = FID + 5;
    public static final int MSG_SIXTH = FID + 6;
    public static final int MSG_SEVENTH = FID + 7;
    public static final int MSG_EIGHTH = FID + 8;

    public interface OnPreExecuteCallback {
        void onPreExecute(int msg);
    }

    public interface OnPostExecuteCallback {
        /**
         * @param obj 获取的结果变量
         * @param msg 用来标识某个request
         */
        void onPostExecute(Object obj, int msg);
    }

    private WeakReference<OnPostExecuteCallback> mWeakCallback;
    private WeakReference<OnPreExecuteCallback> mWeakPreCallback;
    //private OnPostExecuteCallback<Result> mCallback;
    private WeakReference<Activity> mContext;
    protected Context mAppContext;
    private int mMsg;
    //private int mFlag;

    /**
     * @param cb       回调函数，可以通过flag参数指定使用强引用保留还是弱引用保留，典型的调用
     *                 方法是使Activity implements callback，然后将this传入
     * @param msg      {@link #FLAG_WEAK_REFERENCE} 或 {@link #FLAG_STRONG_REFERENCE}
     * @param activity 如果不需要再回调中使用Context，可为null；如果操作与Activity直接关联
     *                 则传入Activity类型；如果是与全局Context关联(文件操作，Toast),传入Context
     *                 .getApplicationContext。 注意：若回调参数的context为Activity实例(不是null),
     *                 则应该先判断isFinishing
     */
    public WeakAsyncTask(Activity activity, OnPostExecuteCallback cb, int msg, OnPreExecuteCallback preCallback) {
        super();
        //mFlag = flag;
        if (activity != null) {
            mAppContext = activity.getApplicationContext();
            mContext = new WeakReference<Activity>(activity);
        }
        if (cb != null) {
            mWeakCallback = new WeakReference<OnPostExecuteCallback>(cb);
        }
        if (preCallback != null) {
            mWeakPreCallback = new WeakReference<OnPreExecuteCallback>(preCallback);
        }
        mMsg = msg;
    }

    public WeakAsyncTask(Activity activity, OnPostExecuteCallback cb, int msg) {
        this(activity, cb, msg, null);
    }

    public WeakAsyncTask(Activity activity) {
        this(activity, null, 0);
    }

    protected Activity getContext() {
        return mContext == null ? null : mContext.get();
    }

    public void setContext(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public void setActivity(Activity activity) {
        mContext = new WeakReference<Activity>(activity);
    }

    /*
     * 在UI线程中被调用
     * @param result doInBackground返回的Result
     * @param context 使用的Context，可能为null
     * @param activityFinishing true表示context为Activity实例且处于销毁中
     */
    protected void onPostExecute(Result result, Activity activity, boolean activityFinishing) {

    }
/*
    public WeakAsyncTask(OnPostExecuteCallback<Result> callback, int flag) {
        this(callback, flag, null);
    }

    public WeakAsyncTask() {
        this(null, 0, null);
    }*/

    @Override
    protected void onPreExecute() {
        OnPreExecuteCallback cb = mWeakPreCallback == null ? null : mWeakPreCallback.get();
        if (cb != null) {
            cb.onPreExecute(mMsg);
        }
    }

    @Override
    protected final void onPostExecute(Result result) {
        /*OnPostExecuteCallback<Result> cb;
        if(mFlag == FLAG_STRONG_REFERENCE) {
            cb = mCallback;
        } else {
            cb = mWeakCallback == null ? null : mWeakCallback.get();
        }*/
        /*if(cb != null) {
            cb.onPostExecute(result, context, isFinishing);
        }*/
        Activity activity = getContext();
        OnPostExecuteCallback cb;
        cb = mWeakCallback == null ? null : mWeakCallback.get();
        if (cb != null) {
            cb.onPostExecute(result, mMsg);
        }
        onPostExecute(result, activity, activity == null ? false : activity.isFinishing());
    }
}
