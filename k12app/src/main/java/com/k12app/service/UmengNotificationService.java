package com.k12app.service;

import org.android.agoo.common.AgooConstants;

import android.content.Context;
import android.content.Intent;

import com.umeng.message.UmengMessageService;
import com.umeng.message.common.UmLog;

/**
 * Created by mitic_xue on 16/10/26.
 */
public class UmengNotificationService extends UmengMessageService implements ICmd{
    @Override
    public void onMessage(Context context, Intent intent) {
        UmLog.d("UmengNotificationService", "onMessage");
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Intent i = new Intent();
        i.putExtra(Key,Push);
        i.setClass(context, LocalService.class);
        i.putExtra("UmengMsg", message);
        context.startService(i);
    }
}
