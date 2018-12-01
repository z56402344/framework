package com.k12app.pay;

import java.lang.ref.WeakReference;
import java.util.Map;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.k12app.bean.PayParams;
import com.k12app.bean.PayParams.AliPayParams;
import com.k12app.bean.PayParams.WxPayParams;
import com.k12app.common.ContantValue;
import com.k12app.db.dao.IUser;
import com.k12app.net.HttpItem;
import com.k12app.net.HttpResp;
import com.k12app.net.IKey;
import com.k12lib.afast.log.Logger;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import z.frame.ICommon;

public class PayController implements WeakAsyncTask.OnPostExecuteCallback, HttpItem.IOKLis, HttpItem.IOKErrLis, IKey {

    public static final String TAG = PayController.class.getSimpleName();
    public static final int FID = 1000;
    public static final int HTTP_GET_ORDER_INFO = FID + 1;
    public static final int HTTP_ALIPAY_RESULT_ID = FID + 2;



    //    1.先调用服务器接口获取订单详情等 2.在调用SDK接口进行支付
    public interface onPayResultCallback {
        //支付结果
        void onPayResult(int status, String type, String msg);

        //点击支付页面确认按钮开始支付
        void onPayStart(String type);

        //开始调用SDK支付接口
        void onSdkPayStart(String type);
    }

    public static final String PAY_WECHAT_APP = "weixin";
    public static final String PAY_UNKNOWN = "";
    public static final String PAY_ALIPAY_APP = "alipay";

    public static final int PAY_RESULT_SUCCESS = 0;
    public static final int PAY_RESULT_FAIL = -1;
    public static final int PAY_RESULT_CANCEL = -2;

    public static final String PARTER_ID_WX_PAY = "1269081801";
    public static final String PARTER_ID_ZHIFUBAO_PAY = "2088002924189861";
    public static final String SELLER_ID_ZHIFUBAO_PAY = "huangjj03@gmail.com";

    public static final String PAY_H5_PATH = "/client/apppay";//根据url path 判断是否是购买的H5

    private static volatile PayController mInstance;
    private static IWXAPI mWxAPI;
    private static String mPayId;
    private static int mExt;

    public static String getPrepayId() {
        return mPayId;
    }

    public static int getExt() {
        return mExt;
    }

    public static PayController getInstance() {
        if (mInstance == null) {
            synchronized (PayController.class) {
                if (mInstance == null) {
                    mInstance = new PayController();
                }
            }
        }
        return mInstance;
    }

    private static onPayResultCallback mCb;
    private WeakReference<Activity> mActivityRef;

    public static void setPayResultCallback(onPayResultCallback cb) {
        mCb = cb;
    }

    public static onPayResultCallback getCallback() {
        return mCb;
    }


    private PayController() {
    }

    public static void init(Activity context) {
        if (mWxAPI == null) {
            mWxAPI = WXAPIFactory.createWXAPI(context, null);
            mWxAPI.registerApp(ICommon.app.mWXAppId);
        }
    }

    private String mPayType;

    /**
     * H5页面点击确认订单之后，调用服务器接口传入套餐id，
     * 支付类型等获取订单详细信息，然后调用SDK进行支付
     *
     * @param payType 支付类型
     * @param pkgId  套餐id
     */
    public void pay(Activity activity, String pkgId, String payType) {
        mPayType = payType;
        mActivityRef = new WeakReference<>(activity);

        if (mCb != null) {
            mCb.onPayStart(payType);
        }
        Logger.i(TAG,"pkgId="+pkgId+", payType="+payType);
        getPayParam(activity, pkgId, payType);
    }

    //调用服务器接口 获取订单详情
    private void getPayParam(Activity activity, String pkgId, String payType) {
        mPayType = payType;

        mActivityRef = new WeakReference<>(activity);
        if (mCb != null) {
            mCb.onPayStart(payType);
        }

        String uid = IUser.Dao.getUser().student_no;
        HttpItem hi = new HttpItem().setListener(this);
        hi.setUrl(ContantValue.F_Recharge);
        hi.setId(HTTP_GET_ORDER_INFO);
//        hi.put("pay_method", payType);
        hi.put("pkg_id", pkgId);
        hi.post(this);

    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        if (mCb != null) {
            mCb.onPayResult(PAY_RESULT_FAIL, PAY_UNKNOWN, TextUtils.isEmpty(errMsg) ? "网络异常，请稍后再试" : errMsg);
        }
    }


    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {

        if (resp.id == HTTP_GET_ORDER_INFO) {

            if (mCb == null) {
                return false;
            }

            PayParams payParams = new PayParams();
            payParams.payType = mPayType;

            if (PayController.PAY_WECHAT_APP.equals(mPayType)) {
                WxPayParams wxParams = resp.getObject(WxPayParams.class, RES);
                payParams.wxPayParams = wxParams;
            } else if (PayController.PAY_ALIPAY_APP.equals(mPayType)) {
                String alipay_str = resp.getString(RES);
                AliPayParams aliPayParams = new AliPayParams();
                aliPayParams.alipay_str = alipay_str;
//                aliPayParams.alipay_str = "app_id=2017030906127359&biz_content=%7B%22body%22%3A%22%E5%AF%8C%E5%BE%B7%E6%B1%87%E9%87%91%E4%BA%A4%E6%98%93%22%2C%22subject%22%3A+%22%E6%B5%8B%E8%AF%95%E5%95%86%E5%93%81%22%2C%22out_trade_no%22%3A+%22181%22%2C%22timeout_express%22%3A+%2230m%22%2C%22total_amount%22%3A+%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fi.www.yifengkj.com%2Fapi%2Fnotify%2FalipayRespond&sign_type=RSA2&timestamp=2017-03-26+13%3A57%3A18&version=1.0&sign=BDVtpkayF0mNG1Q4CZrh8fGqt%2FMpBtSJbRf1IKTTpsTPfqsqorAGPpQwN1rk5%2FtiIDul1I5UZsZPaKyomAwkPeARtgpFU7gTpuATfhiNQMo%2BWVysCkb%2FDQ0ju47K10PwnIKag53Zlz4yuv8ELMni8wTUdmr8fOOPm7GJcFSmzgiOVz44kfJ0nNRTRdt1Zca6aPsXazXVsBDRYBuyBXyBctC%2ByPojY%2Bj9mdA1kWs%2FpbkikCqoP698BpephLzYpL9nGjfQQfWNodJsNBGIaFEHgl2Ioc7PMJG7kzH2VSw0iFw0CjJ3zk8tusUTXJJDe4uVRz0Ynj6SXlbKk1rsREr8AQ%3D%3D";
                payParams.aliPayParams = aliPayParams;
            }

            if (payParams.wxPayParams == null && payParams.aliPayParams == null) {
                if (mCb != null) {
                    mCb.onPayResult(PAY_RESULT_FAIL, PAY_UNKNOWN, null);
                }
                mCb = null;
                return false;
            }
            paySdkStart(payParams);

        }
        return false;

    }


    /**
     * 唤起SDK支付
     *
     * @param payParams
     */
    private void paySdkStart(PayParams payParams) {
        Log.i("lyy", "payParams.payType>>>>>>> " + payParams.payType);

        if (PayController.PAY_WECHAT_APP.equals(payParams.payType)) {
            if (mCb != null) {
                mCb.onSdkPayStart(PAY_WECHAT_APP);
            }

            WxPayParams wxPayParams = payParams.wxPayParams;

            if (wxPayParams == null) {
                if (mCb != null) {
                    mCb.onPayResult(PAY_RESULT_FAIL, PAY_UNKNOWN, null);
                }
                mCb = null;
                return;
            }
            Log.i("lyy", "唤起微信支付SDK >>>>>>> ");
//            //微信SDK支付
            PayReq request = new PayReq();
//            request.appId = "wxf9307c265db674ef";
//            request.partnerId = "1448271802";
//            request.prepayId = "wx2017032418491377c54b27aa0154801017";
//            request.packageValue = "Sign=WXPay";
//            request.nonceStr = "n3977lqycpc67o4xljpzcj8zjt5l0ksk";
//            request.timeStamp = "1490352553";
//            request.sign = "0E2AFA9BA32321F3457371606C2506C8";
            request.appId = wxPayParams.appid;
            request.partnerId = wxPayParams.partnerid;
            request.prepayId = wxPayParams.prepayid;
            request.packageValue = wxPayParams.packageValue;
            request.nonceStr = wxPayParams.noncestr;
            request.timeStamp = wxPayParams.timestamp;
            request.sign = wxPayParams.sign;
            mWxAPI.sendReq(request);

        } else if (PayController.PAY_ALIPAY_APP.equals(payParams.payType)) {

            if (mCb != null) {
                mCb.onSdkPayStart(PAY_ALIPAY_APP);
            }

            AliPayParams aliPayParams = payParams.aliPayParams;

            if (aliPayParams == null) {
                if (mCb != null) {
                    mCb.onPayResult(PAY_RESULT_FAIL, PAY_UNKNOWN, null);
                }
                mCb = null;
                return;
            }



            new AliPaySdkTask(mActivityRef.get(), this, aliPayParams.alipay_str, WeakAsyncTask.MSG_SECOND).execute();

        }
    }


    /**
     * 说明：调用支付宝的SDK支付接口
     */
    private static class AliPaySdkTask extends WeakAsyncTask<Void, Void, Map<String, String>> {
        private String mPayInfoStr;

        public AliPaySdkTask(Activity activity, OnPostExecuteCallback cb, String payInfo, int msg) {
            super(activity, cb, msg);
            mPayInfoStr = payInfo;
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {

            Activity activity = getContext();
            if (activity == null) {
                return null;
            }
            PayTask payTask = new PayTask(activity);
            Log.i("lyy", "支付宝支付串  >>>>> " + mPayInfoStr);
            Log.i("lyy", "开发版本号  >>>>> " + payTask.getVersion());

            Map<String, String> stringStringMap = payTask.payV2(mPayInfoStr, true);
            // 调用支付接口，获取支付结果
//            String result = payTask.pay(mPayInfoStr, true);//true : 支付过程中是否有loading效果
            Log.i("lyy", "stringStringMap >>>>>>> " + stringStringMap.toString());
//            AliPayResult payResult = new AliPayResult(result);
            return stringStringMap;
        }
    }

    @Override
    public void onPostExecute(Object obj, int msg) {

        //支付宝支付结果
        if (msg == WeakAsyncTask.MSG_SECOND) {
            Map<String, String> aliPayResult = (Map<String, String>) obj;
            if (aliPayResult == null) {
                if (mCb != null) {
                    mCb.onPayResult(PAY_RESULT_FAIL, PAY_UNKNOWN, null);
                }
                mCb = null;
                return;
            }

            if (mCb == null) return;

            PayResult payResult = new PayResult((Map<String, String>) aliPayResult);
            /**
             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                mCb.onPayResult(PAY_RESULT_SUCCESS, PayController.PAY_ALIPAY_APP, null);
                mCb = null;
            } else {
                mCb.onPayResult(PAY_RESULT_FAIL, PayController.PAY_ALIPAY_APP, null);
                mCb = null;
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            }

            return;
        }
    }
}
