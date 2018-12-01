package com.k12app.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.k12lib.afast.log.Logger;


/**
 * 发广播停止协议播放器的执行
 */
public class ListenReceiver extends BroadcastReceiver {
    public static final String mKPlayID = "playid";
    public static final int TYPE_STOP = 0;
    public static final int TYPE_PLAY = 3;

    public static final String MUSIC_ACTION = "com.android.music.musicservicecommand.pause";
    private static final String TAG = ListenReceiver.class.getSimpleName();

    private Context mCtx;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            int playid = intent.getIntExtra("playid", 0);
            Logger.i(TAG,"recv a ListenMgr broadcast:" + playid);

            if(playid != TYPE_PLAY){
//                ListenMgr.instance().stop();
            }
        }
    }

    // 初始化
    public void init(Context ctx) {
        if (mCtx!=null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_ACTION);
        ctx.registerReceiver(this,filter);
        mCtx = ctx;
    }

    // 销毁
    public void destroy() {
        if (mCtx==null) return;
        mCtx.unregisterReceiver(this);
        mCtx = null;
    }

    public static void pauseMusic(Context context, int id) {
        Logger.i(TAG,"发送一次Play的广播  >>> ");
        Intent freshIntent = new Intent();
        freshIntent.setAction(MUSIC_ACTION);
        freshIntent.putExtra("command", "pause");
        freshIntent.putExtra(mKPlayID,id);
        context.sendBroadcast(freshIntent);
    }
}
