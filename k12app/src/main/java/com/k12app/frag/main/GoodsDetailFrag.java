package com.k12app.frag.main;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.k12app.R;
import com.k12app.core.FidAll;
import com.k12app.core.IAct;
import com.k12app.net.HttpIFCtx;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12app.pay.PayController;
import com.k12lib.afast.log.Logger;

import z.ext.frame.ZWorkThread;
import z.frame.BaseFragment;
import z.frame.UmBuilder;
import z.http.ZHttpParams;


public class GoodsDetailFrag extends WebViewFrag implements PayController.onPayResultCallback, IKey, IAct {

    public static final String TAG = GoodsDetailFrag.class.getSimpleName();
    public static final int FID = FidAll.GoodsDetailFrag;
    public static final int IA_OneByOne = FID + 3;

    public static final int Permissions_1V1 = 2;//主站1v1套餐权限

    private ValueCallback<Uri> mUploadMsg;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int ALBUM_REQUEST_CODE = 1;// 表单的结果回调
    private int mExitWebview = 0;//点击返回键是否退出整个webview

    @Override
    protected boolean shouldOverrideUrl(WebView view, String url) {
        if (TextUtils.isEmpty(url)) return false;
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (path.equalsIgnoreCase(PayController.PAY_H5_PATH)) {
            //准备支付的url，需要截取参数传给服务器,回调中再开始支付、
            //截取url的参数
            int idx = url.indexOf("?");
            if (idx == 0) return true;
            String extId = uri.getQueryParameter("extId");
            String payType = uri.getQueryParameter("payType");
            String oneExtId = uri.getQueryParameter("oneExtId");

            PayController.init(getActivity());
            PayController.setPayResultCallback(this);
            PayController.getInstance().pay(getActivity(), extId, payType);

            return true;
        } else if (url.contains("/client/invoke?type=1")) {
            //调用js方法传入是否安装微信
            mWb.loadUrl("javascript:checkWeixin('" + (UmBuilder.isWXAppInstalledAndSupported(getActivity()) ? 1 : 0) + "')");
            return true;
        }
        return false;
    }


    @Override
    public void onPayResult(int status, String type, String msg) {
        mExitWebview = 1;
        handlePay(this, mWb, status, type, msg, true, null);
    }


    /**
     * @param classUrl 课中支付的Url，如果不为Null，则是课中的支付
     */
    public static void handlePay(BaseFragment bf, WebView wb, int status, String type, String msg, boolean isLoadUrl, String classUrl) {
        bf.hideLoading();
        PayController.setPayResultCallback(null); //help gc
        if (status == PayController.PAY_RESULT_SUCCESS) {
            //刷新webview控件
            if (isLoadUrl) {
                if (!TextUtils.isEmpty(classUrl)) {
                    Logger.i(TAG, "classUrl >>>" + classUrl);
                    wb.loadUrl(classUrl);
                } else {
                    ZHttpParams p = new ZHttpParams();
                    p.put("payId", PayController.getPrepayId());
                    HttpIFCtx.instance().addPublicParams(p);
//                    String url = ContantValue.F_GETPAYRESULT + p.toOrderString();
//                    wb.loadUrl(url);
                }
            } else {
                wb.reload();
            }

            ZWorkThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //TODO 刷新用户各种状态

                }
            }, 15000);

        } else if (status == PayController.PAY_RESULT_CANCEL) {
            bf.showShortToast("取消支付");
        } else {
            bf.showShortToast(TextUtils.isEmpty(msg) ? "操作失败，请重试" : msg);
        }
    }

    @Override
    public void onPayStart(String type) {
        //
        showLoading(true);
//        showShortToast("开始");
    }

    @Override
    public void onSdkPayStart(String type) {
        hideLoading();
//        showShortToast("调用SDK进行支付");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PayController.setPayResultCallback(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                //只有支付成功时退出整个webView
                if (mExitWebview == 1) {
                    pop(true);
                    return;
                }

                if (mWb.canGoBack()) {
                    try {
                        mWb.goBack();
                    } catch (Exception e) {
                        e.printStackTrace();
                        pop(true);
                    }
                    // 返回键退回
                } else {
                    if (isEnterHome) {
                        notifyActivity(SplashFrag.FExit, 0, null);
                    } else {
                        pop(true);
                    }
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {

        if (isEnterHome) {
            notifyActivity(SplashFrag.FExit, 0, null);
        }

        //只有支付成功是退出整个webview
        if (mExitWebview == 1) {
            pop(true);
            return true;
        }

        try {
            if (mWb.canGoBack()) {
                mWb.goBack();
                // 返回键退回
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //--------------------------选择相册-------------------------------------
    @Override
    public void setWebConfig() {
        super.setWebConfig();
        try {
//            mWb.addJavascriptInterface(new JSObject(mRoot.getContext()),"android");
            mWb.setWebChromeClient(mWCC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    WebChromeClient mWCC = new WebChromeClient() {

        public void onRequestFocus(WebView view) {
            super.onRequestFocus(view);
            view.requestFocus();
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if ((mFlags & fTitle) == fTitle) {
                setTitleText(title);
                mShareTitle = title;
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            //地理位置回调
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams
//                fileChooserParams) {
//            mUploadCallbackAboveL = filePathCallback;
//            take();
//            return true;
//        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMsg = uploadMsg;
            take();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMsg = uploadMsg;
            take();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMsg = uploadMsg;
            take();
        }
    };


    private void take() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > 19 || (Build.VERSION.SDK_INT >= 19 && Build.MANUFACTURER != null && Build.MANUFACTURER.compareToIgnoreCase("vivo") == 0)) {
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
        }
        startActivityEx(intent, ALBUM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getActivity() == null) return;
        _log("onActivityResult 111111");
        if (requestCode == ALBUM_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (null == mUploadMsg && null == mUploadCallbackAboveL) return;
//            Uri result = data == null || resultCode != getActivity().RESULT_OK ? null : data.getData();
            Uri result = null;
            String uriStr = null;
            try {
//                uriStr = PicSelect.getPath(getActivity().getContentResolver(), data.getData());
                result = Uri.parse(uriStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMsg != null) {
                if (result != null) {
                    try {
//                        String path = getPath(getActivity().getApplicationContext(),result);
                        Uri uri = Uri.fromFile(new File(uriStr));
                        mUploadMsg.onReceiveValue(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mUploadMsg = null;
                    }
                } else {
                    try {
                        mUploadMsg.onReceiveValue(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mUploadMsg = null;
                    }
                }
            }
        }
    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != ALBUM_REQUEST_CODE || mUploadCallbackAboveL == null) {
            return;
        }
        try {
            _log("onActivityResultAboveL 2222222");
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
//                if (data == null) {
//                    results = new Uri[]{mImgUri};
//                } else {
                String dataString = data.getDataString();
                if (Build.VERSION.SDK_INT > 15) {
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
//                }
            }
            if (results != null) {
                try {
                    mUploadCallbackAboveL.onReceiveValue(results);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mUploadCallbackAboveL = null;
                }
            } else {
                try {
                    mUploadCallbackAboveL.onReceiveValue(null);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mUploadCallbackAboveL = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


}
