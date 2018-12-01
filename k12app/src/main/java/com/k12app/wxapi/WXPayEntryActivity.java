package com.k12app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.k12app.pay.PayController;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import z.frame.ICommon;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    public static final String TAG = WXPayEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, ICommon.app.mWXAppId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG ," WXPayEntryActivity resp.errCode >>>>>> " + resp.errCode);
        Log.i(TAG," WXPayEntryActivity resp.errStr >>>>>> " + resp.errStr);

        PayController.onPayResultCallback cb = PayController.getCallback();
        if (cb == null) {
            finish();
            return;
        }

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                //success
                cb.onPayResult(PayController.PAY_RESULT_SUCCESS, PayController.PAY_WECHAT_APP, null);
            } else if (resp.errCode == -1) {
                //fail
                cb.onPayResult(PayController.PAY_RESULT_FAIL, PayController.PAY_WECHAT_APP, null);
            } else if (resp.errCode == -2) {
                //cancel
                cb.onPayResult(PayController.PAY_RESULT_CANCEL, PayController.PAY_WECHAT_APP, null);
            } else {
                //we should consider all other unknown codes "success"
                cb.onPayResult(PayController.PAY_RESULT_SUCCESS, PayController.PAY_WECHAT_APP, null);
            }
            PayController.setPayResultCallback(null);
        }
        finish();
    }
}
