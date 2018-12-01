package com.k12app.activity;

import android.os.Bundle;

import com.k12lib.afast.log.Logger;
import com.umeng.socialize.media.WBShareCallBackActivity;

/**
 *微博回调
 */
public class WBShareActivity extends WBShareCallBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.i("微博回调  onCreate >>> ");
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            if (!isFinishing()) {
                finish();
            }
            e.printStackTrace();
        }
    }
}
