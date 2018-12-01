package com.k12app.wxapi;

import android.os.Bundle;

import com.k12lib.afast.log.Logger;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * 说明：微信回调的Activity
 */
public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onStart() {
        Logger.i("微信回调开始  onStart >>> ");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.i("微信回调  onCreate >>> ");
//		SnsPostListener mSnsPostListener = new SnsPostListener() {
//
//			@Override
//			public void onStart() {
//
//			}
//
//			@Override
//			public void onComplete(SHARE_MEDIA platform, int stCode,
//					SocializeEntity entity) {
//				if (stCode == 200) {
//					Toast.makeText(WXEntryActivity.this, "分享成功",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(WXEntryActivity.this,
//							"分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
//							.show();
//				}
//
//			}
//		};
//		UMServiceUtil.getInstance(this).mController
//				.registerListener(mSnsPostListener);
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
