package com.k12app.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.k12app.common.GlobaleParms;
import com.k12app.net.HttpIFCtx;
import com.k12app.service.UmengNotificationService;
import com.k12lib.afast.log.Logger;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;

import orgx.apache.http.client.config.RequestConfig;
import z.db.ShareDB;
import z.ext.frame.ZBaseApp;
import z.http.ZHttpCenter;

import static com.k12app.common.IAutoParams.kPushId;
import static com.k12app.common.IAutoParams.kSec;

public class MainApplication extends ZBaseApp {

    protected static final String TAG = MainApplication.class.getSimpleName();
    public static MainApplication mApplication;
    private MainProcInfo mInfo = null;
//    private Handler handler;
//    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";


    public static Context getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        mApplication = this;
        super.onCreate();
        initUmengPush();
        printUmengKey();
    }

    @Override
    protected void onInit(String packageName,String procName) {
        if (procName==null||packageName.equals(procName)) {
            super.onInit(packageName,procName);
            // 只有主进程初始化这些信息
            mInfo = new MainProcInfo();
            mInfo.init(this);
            RequestConfig cfg = RequestConfig.custom().setConnectionRequestTimeout(5000).setSocketTimeout(25000).setConnectTimeout(5000).build();
            ZHttpCenter.createInstance(cfg, null);
            ZHttpCenter.setDebug(GlobaleParms.isDebug);
        } else if (procName.equals(packageName+":pushservice")) {
            super.onInit(packageName,packageName);
            // :pushservice进程
        } else {
            super.onInit(packageName,procName);
        }
    }

    public void initUmengPush(){
        PushAgent mPushAgent = PushAgent.getInstance(this);

        mPushAgent.setDebugMode(false);
        mPushAgent.setResourcePackageName("com.k12app");

        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);

//		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);

//        String umengAppKey = GlobaleParms.isTeacher?GlobaleParms.UmengAppKey[1]:GlobaleParms.UmengAppKey[0];
//        String umengSecret = GlobaleParms.isTeacher?GlobaleParms.UmengSecret[1]:GlobaleParms.UmengSecret[0];
//        mPushAgent.setAppkeyAndSecret(umengAppKey,umengSecret);
//        mPushAgent.setMessageChannel("normal");

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Logger.i(TAG, "register device token umeng_id: " + deviceToken);
//                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
                HttpIFCtx.saveCid(deviceToken);
//                ShareDB.Sec sec = new ShareDB.Sec(kSec);
//                sec.load();
//                sec.put(kPushId, deviceToken);
//                sec.save(false);
            }

            @Override
            public void onFailure(String s, String s1) {
                Logger.i(TAG, "register failed: " + s + " " +s1);
//                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

        mPushAgent.setPushIntentServiceClass(UmengNotificationService.class);
    }


    @Override
    public void onTerminate() {
        if (mInfo!=null) {
            mInfo.uninit();
            mInfo = null;
            ZHttpCenter.destroyInstance();
        }
        super.onTerminate();
    }

    public void printUmengKey(){
        String umeng_appkey;
        String umeng_message_secret;
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);

            umeng_appkey = appInfo.metaData.getString("UMENG_APPKEY");
            umeng_message_secret = appInfo.metaData.getString("UMENG_MESSAGE_SECRET");

            Logger.d("UMENG_APPKEY=" + umeng_appkey +"\n UMENG_MESSAGE_SECRET=" + umeng_message_secret);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
    }
}
