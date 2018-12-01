package com.k12app.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.k12app.R;
import com.k12app.bean.AppVersionBean;
import com.k12app.core.DownWrapper;
import com.k12app.net.SyncMgr;
import com.k12app.service.ICmd;
import com.k12lib.afast.log.Logger;

import z.down.DMgr;

/**
 * 说明：下载APK
 */
public class DownApkManager implements ICmd {
    private static final String TAG = DownApkManager.class.getSimpleName();
    // 下载APK成员变量
    private NotificationCompat.Builder mBuilder;
    private RemoteViews mRemoteViews;
    private int mProgress = 0;
    /**
     * Notification下载APK的ID
     */
    private int mNotifyId = 100;
    private String mDownId = null;
    private NotificationManager mManager = null;
    private Context mContext;
    private Handler mHandler = null;

    //下载APK的逻辑
    public boolean initDownAPK(Context context, NotificationManager manager, Handler handler) {
        if (mDownId == null) {
            Logger.i(TAG, "启动下载APK服务");
            mManager = manager;
            this.mContext = context;
            this.mHandler = handler;
            AppVersionBean ver = SyncMgr.getVer();
            if (ver != null && !TextUtils.isEmpty(ver.version) && !TextUtils.isEmpty(ver.version_url)) {
                Logger.i(TAG,"ver.version_url >>>>> " + ver.version_url);
                initNotification("准备下载");
                fireUpdateTimer(ver);
                return true;
            }
        }
        return false;
    }

    private void initNotification(String status) {
        mBuilder = new NotificationCompat.Builder(mContext);
        mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notifi_down_app);
        mRemoteViews.setImageViewResource(R.id.custom_progress_icon, R.mipmap.ic_launcher);
        mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, status);
        if (mProgress >= 100) {
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 0, 0, false);
            mRemoteViews.setViewVisibility(R.id.custom_progressbar, View.GONE);
        } else {
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100,
                    mProgress, false);
            mRemoteViews.setViewVisibility(R.id.custom_progressbar,
                    View.VISIBLE);
        }
        mBuilder.setContent(mRemoteViews)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                // .setNumber(number)//显示数量
                .setPriority(Notification.PRIORITY_HIGH)// 设置该通知优先级
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setSmallIcon(R.mipmap.ic_launcher).setTicker("下载 " + mContext.getResources().getString(R.string.app_name) + " 客户端");
        mBuilder.setContentIntent(getDefaultIntent(Notification.FLAG_ONGOING_EVENT));
        Notification nitify = mBuilder.build();
        nitify.contentView = mRemoteViews;
        mManager.notify(mNotifyId, nitify);
    }

    /**
     * 设置下载进度
     */
    public void updateNotify(int progress) {
        if (progress < 0) {
            // 下载失败
            mProgress = -1;
            if (mDownId != null) {
                mHandler.removeCallbacks(mUpdateTimer);
                mHandler.sendEmptyMessage(DOWN_APK);
                mDownId = null;
            }
            mManager.cancel(mNotifyId);// 删除一个特定的通知ID对应的通知
            return;
        }

        if (progress >= 100) {
            mProgress = 100;
            mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, "下载完成");
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 0, 0, false);
            mRemoteViews.setViewVisibility(R.id.custom_progressbar, View.GONE);
            mManager.cancel(mNotifyId);// 删除一个特定的通知ID对应的通知
            if (mDownId != null) {
                mHandler.removeCallbacks(mUpdateTimer);
                mHandler.sendEmptyMessage(DOWN_APK);
                mDownId = null;
            }
        } else {
            if (mProgress == 0) {
                mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, "下载中");
                mRemoteViews.setViewVisibility(R.id.custom_progressbar, View.VISIBLE);
            }
            mProgress = progress;
            mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, mProgress, false);
            mManager.notify(mNotifyId, mBuilder.build());
        }
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     * Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefaultIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, new Intent(), flags);
        return pendingIntent;
    }

    private Runnable mUpdateTimer = new Runnable() {
        @Override
        public void run() {
            if (mDownId == null)
                return;
            // 更新进度条
            updateNotify(DMgr.queryProgress(mDownId));
            if (mProgress > -1 && mProgress < 100) {
                mHandler.postDelayed(this, 500);
            }
        }
    };

    // 触发定时器更新进度条
    private void fireUpdateTimer(AppVersionBean ver) {
        if (mDownId == null) {
            mDownId = DownWrapper.downApk(ver.version, ver.version_url);
        }
        mHandler.removeCallbacks(mUpdateTimer);
        mHandler.postDelayed(mUpdateTimer, 500);
    }
}
