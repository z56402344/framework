package com.k12app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.k12app.R;
import com.k12app.activity.HomeAct;
import com.k12app.activity.SecondAct;
import com.k12app.activity.SplashActivity;
import com.k12app.common.GlobaleParms;
import com.k12app.core.FidAll;
import com.k12app.db.dao.IUser;
import com.k12app.frag.main.TabHomeFrag;
import com.k12app.frag.main.WebViewFrag;
import com.k12app.utils.DownApkManager;
import com.k12lib.afast.log.Logger;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.util.Map;

import z.db.ShareDB;
import z.frame.LocalCenter;

/**
 * Created by mitic_xue on 16/10/28.
 */
public class LocalService extends Service implements ICmd {

    public static final int FID = FidAll.LocalServiceFID;
    public static final int IA_ToSelecteTeacherTea = FID+1;
    public static final int IA_ToTeacherEndCoaching = FID+2;
    public static final int IA_ToRepltimeTea = FID+3;
    public static final int IA_ToRepltimeStu = FID+4;
    public static final int IA_ToReward = FID+5;


    private static final String TAG = LocalService.class.getName();
    public static UMessage oldMessage = null;
    private NotificationManager mManager = null;
    private DownApkManager mDownApkManager = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DOWN_APK:
                if (mDownApkManager != null) {
                    mDownApkManager = null;
                }
                break;
            default:
                break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        _log("onStartCommand:" + flags + "," + startId);
        startId = super.onStartCommand(intent, flags, startId);
        if (intent == null) return startId;
        int msg_type = intent.getIntExtra(Key, 0);
        switch (msg_type) {
        case Push:
            onRecvPushMsg(intent);
            break;
        case NotifyClick:
            onClickNotification(intent);
            break;
        case DOWN_APK:
            if (mDownApkManager == null) {
                mDownApkManager = new DownApkManager();
                boolean initDownAPK = mDownApkManager.initDownAPK(this,
                        mManager, mHandler);
                if (!initDownAPK) {
                    mDownApkManager = null;
                }
            }
            break;
        }
        return startId;
    }

    private void _log(String txt) {
        Logger.w("LocalService", txt);
    }


    public void onRecvPushMsg(Intent intent) {
        try {
            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra("UmengMsg");
            UMessage msg = new UMessage(new JSONObject(message));

            // 对完全自定义消息的处理方式，点击或者忽略
//            boolean isClickOrDismissed = true;
//            if (isClickOrDismissed) {
//                //完全自定义消息的点击统计
//                UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//            } else {
//                //完全自定义消息的忽略统计
//                UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
//            }

            if (msg == null) {
                _log("msg == null");
                return;
            }

            Map<String, String> extra = msg.extra;
            if (extra == null) {
                _log("extra == null");
                return;
            }

            int opt = Integer.valueOf(extra.get(Opt));
            String id = extra.get(ID);
            String linkUrl = extra.get(LinkUrl);
            _log("友盟推送消息 opt=" + opt + " ,id=" + id + " ,link_url=" + linkUrl);

            ShareDB.Sec mSec = new ShareDB.Sec(SwiSec);
            mSec.load();
            boolean isON = mSec.getBoolean(SwiSec+2);
            int isFirst = mSec.getInt(IsFirst, 0);
            _log("友盟推送消息 isFirst=" + isFirst + " ,isON=" + isON);
            if (isFirst == 1 && !isON){
                _log("推送消息关闭 >>> ");
                return;
            }

            Intent i = null;

            switch (opt) {
            case ToH5: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToActivityTea:
            case ToActivityStu: {
                if (TextUtils.isEmpty(linkUrl)) {
                    i = new Intent(this, LocalService.class);
                } else {
                    i = new Intent(this, LocalService.class);
                }
                break;
            }
            case ToCouponTea:
            case ToCouponStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToReward:
                //打赏通知
                Logger.i(TAG,"ToReward >>> 打赏通知");
                LocalCenter.send(IA_ToReward, 0, id+"#"+msg.text);
                i = new Intent(this, LocalService.class);
                break;
            case ToRepltimeTea:
                Logger.i(TAG,"ToRepltimeTea >>> 有学生发布即时问");
                LocalCenter.send(IA_ToRepltimeTea, 0, null);
                i = new Intent(this, LocalService.class);
                break;
            case ToRepltimeStu:{
                LocalCenter.send(IA_ToRepltimeStu, 0, null);
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToSelecteTeacherTea: {
                Logger.i(TAG,"ToSelecteTeacherTea >>> ");
                LocalCenter.send(IA_ToSelecteTeacherTea, 0, null);
                i = new Intent(this, LocalService.class);
                break;
            }
            case NotToSelecteTeacherTea:{
                Logger.i(TAG,"NotToSelecteTeacherTea >>> ");
                LocalCenter.send(IA_ToSelecteTeacherTea, 1, null);
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToTeacherEndCoaching:{
                Logger.i(TAG,"ToTeacherEndCoaching >>> 学生已结束辅导,老师同步结束辅导");
                if (!GlobaleParms.isTeacher){
                    Logger.i(TAG,"ToTeacherEndCoaching >>> 学生端不发送此广播");
                    return;
                }
                LocalCenter.send(IA_ToTeacherEndCoaching, 0, null);
                i = new Intent(this, LocalService.class);
                break;
            }
            case To1V1Tea: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToCancel1V1Tea:
            case ToCancel1V1Stu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToFightCourseStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToFightCourseSuccessTea:
            case ToFightCourseSuccessStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToFightCourseCancelTea:
            case ToFightCourseCancelStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToBeforeClassTea:
            case ToBeforeClassStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToMirClassCancelStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToMirBeforeClassTea:
            case ToMirBeforeClassStu: {
                i = new Intent(this, LocalService.class);
                break;
            }
            case ToStuCloseRealtime:
            case ToTeacherCloseRealtime: {
                i = new Intent(this, LocalService.class);
                break;
            }
            default:
                i = new Intent(this, LocalService.class);
                break;
            }
            if (i != null) {
                i.putExtra(LinkUrl, linkUrl);
                i.putExtra(KPushT, opt);
                i.putExtra(Key, NotifyClick);
                i.putExtra(ID, id);
            }

            String title = msg.title;
            if (TextUtils.isEmpty(title)) {
                title = getString(R.string.app_name);
            }

            sendNotification(title, msg.text, i);

        } catch (Exception e) {
            UmLog.e(TAG, e.getMessage());
        }
    }

    // 响应通知栏的点击 并跳转到界面去
    private void onClickNotification(Intent it) {
        int t = it.getIntExtra(KPushT, -1);
        if (t == -1) return;
        // 推送显示率
//		UmengErr.reportSimple(IUmEvt.GeTuiClick,String.valueOf(t));
        Intent i = new Intent(Intent.ACTION_MAIN);
        String linkUrl = it.getStringExtra(LinkUrl);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        if (IUser.Dao.checkAutoLogin()) {
            switch (t) {
            case ToH5: { //1.跳转到H5页面的push：{"t":1,"url":"H5页面地址，比如：http://m.baidu.com/"}
                i.setClass(this, SecondAct.class);
                i.putExtra(SecondAct.kFID, WebViewFrag.FID);
                i.putExtra(WebViewFrag.kTitle, "活动");
                i.putExtra(WebViewFrag.kUrl, linkUrl);
                i.putExtra(WebViewFrag.kFlags, WebViewFrag.fCookies | WebViewFrag.fTitle | WebViewFrag.fShowClose);
                break;
            }
            case ToActivityTea:
            case ToActivityStu: {
                if (TextUtils.isEmpty(linkUrl)) {
                    i.setClass(this, HomeAct.class);
                    it.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                } else {
                    i.setClass(this, SecondAct.class);
                    i.putExtra(SecondAct.kFID, WebViewFrag.FID);
                    i.putExtra(WebViewFrag.kTitle, "活动");
                    i.putExtra(WebViewFrag.kUrl, linkUrl);
                    i.putExtra(WebViewFrag.kFlags, WebViewFrag.fCookies | WebViewFrag.fTitle | WebViewFrag.fShowClose);
                }
                break;
            }
            case ToReward:
                //打赏通知
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            case ToRepltimeStu:
            case ToRepltimeTea: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToSelecteTeacherTea: {
                LocalCenter.send(IA_ToSelecteTeacherTea, 0, null);
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case NotToSelecteTeacherTea:
                LocalCenter.send(IA_ToSelecteTeacherTea, 1, null);
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            case ToTeacherEndCoaching:
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            case To1V1Tea: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToCancel1V1Tea:
            case ToCancel1V1Stu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToFightCourseStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToFightCourseSuccessTea:
            case ToFightCourseSuccessStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToFightCourseCancelTea:
            case ToFightCourseCancelStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToBeforeClassTea:
            case ToBeforeClassStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToMirClassCancelStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToMirBeforeClassTea:
            case ToMirBeforeClassStu: {
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            case ToStuCloseRealtime:
            case ToTeacherCloseRealtime:{
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
            default:
                i.setClass(this, HomeAct.class);
                i.putExtra(HomeAct.kFID, TabHomeFrag.FID_PUT_ON_TOP);
                break;
            }
        } else {
            Logger.i("LocalService", "未登录打开Splash页面 >>> ");
            i.setClass(this, SplashActivity.class);
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pi.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送到通知栏
    private void sendNotification(String title, String txt, Intent it) {
        PendingIntent pi = PendingIntent.getService(this, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setTicker(txt)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi)
                .setContentTitle(title)
                .setContentText(txt)
                .setAutoCancel(true);
        try {// 避免某些系统上的异常
            mManager.notify(R.string.app_name, builder.build());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
