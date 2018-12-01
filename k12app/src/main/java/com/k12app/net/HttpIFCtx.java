package com.k12app.net;


import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.k12app.bean.UserBean;
import com.k12app.common.IAutoParams;
import com.k12app.core.IAct;
import com.k12app.db.dao.IUser;
import com.k12app.utils.Base64Util;
import com.k12app.utils.ThreeDes;
import com.k12lib.afast.log.Logger;

import z.db.ShareDB;
import z.ext.base.ZGlobalMgr;
import z.frame.JsonTree;
import z.http.ZHttpParams;
import z.util.MD5;


// 接口上下文
public class HttpIFCtx implements IAct{
    private static final String Key = "HttpIFCtx";
    private static final String TAG = HttpIFCtx.class.getSimpleName();
    private static final String KPwd = "password";
    private static final String KOldPwd = "old_password";
    private static final String KNewPwd = "new_password";

    // 由外部初始化一次,不自动生成这个全局对象
    private String phoneType;
//    private String deviceType;
//    private String deviceId;
    private String appVer;
    private String systemVer;
//    private String channel;
    private String pushId = "";

    public static HttpIFCtx instance() {
        HttpIFCtx ctx = ZGlobalMgr.getGlobalObj(Key);
        if (ctx == null) {
            ctx = new HttpIFCtx();
            ZGlobalMgr.setGlobalObj(Key, ctx);
        }
        return ctx;
    }

    public HttpIFCtx() {
        initPublicParams();
    }

    private void initPublicParams() {
        ShareDB.Sec sec = new ShareDB.Sec(IAutoParams.kSec);
//        channel = sec.getString(IAutoParams.kVendor);
        appVer = sec.getString(IAutoParams.kVerS);
        systemVer = sec.getString(IAutoParams.kVerS);
//        deviceId = sec.getString(IAutoParams.kDevId);
        phoneType = "android";
        systemVer = android.os.Build.VERSION.RELEASE;
//        deviceType = android.os.Build.MODEL;
        pushId = sec.getString(IAutoParams.kPushId);
        if (TextUtils.isEmpty(pushId)) {
//            pushId = PushAgent.getInstance(app.ctx).getRegistrationId();
//            sec.put(kPushId, pushId);
//            sec.save(false);
            pushId = "0";
        }
    }

    public void addPublicParams(ZHttpParams params) {
//		params.put("phoneType", phoneType);
//		params.put("deviceType", deviceType);
//		params.put("deviceId", deviceId);
//		params.put("appVer", appVer);
//		params.put("systemVer", systemVer);
//        if (TextUtils.isEmpty(pushId)){
//            ShareDB.Sec sec = new ShareDB.Sec(IAutoParams.kSec);
//            sec.load();
//            pushId = sec.getString(IAutoParams.kPushId);
//            Logger.i(TAG,"umeng_id = null,重新获取 umeng_id="+pushId);
//        }

        String tranid = Base64Util.getTranid();
        params.put("os", 0);//0=android,1-iOS
        params.put("umeng_id", pushId);//友盟推送id
		params.put("tranid", tranid);//yyyyMMddHHmmssSSS,不足部分补0
        String pwd = params.getString(KPwd);
        String oldPwd = params.getString(KOldPwd);
        String newPwd = params.getString(KNewPwd);
        isDes(params,pwd,KPwd,tranid);
        isDes(params,oldPwd,KOldPwd,tranid);
        isDes(params,newPwd,KNewPwd,tranid);

        UserBean userbean = IUser.Dao.getUser();
        if (userbean != null){
            params.put("token", userbean.token);//友盟id
        }
//		appKey : DB0DF84A4AD7C060722CC6BF99882A67
        String str = params.toOrderString().append("DB0DF84A4AD7C060722CC6BF99882A67").toString();
        String auth = MD5.md5Upper(str);
        params.put("auth", auth);
        String jsonParams = getJson(params);
        params.urlParams.clear();
        params.put("text",jsonParams);
    }

    //对有密码字段的进行加密处理
    public void isDes(ZHttpParams params,String pwd,String key,String tranid){
        if (!TextUtils.isEmpty(pwd)){
            params.put(key, Base64Util.getBase64(ThreeDes.encode(pwd,tranid)) );
        }
    }

    public static String getJson(ZHttpParams params){
        JSONObject json = new JSONObject();
        for (String key : params.urlParams.keySet()) {
            ZHttpParams.ZParam zParam = params.urlParams.get(key);
            Logger.i(TAG,key+"="+ zParam.value);
            if (key.equals("img_list") || key.equals("intro_img_json_array") || key.equals("schedule_id_list") ){
//                json.put(key, JsonTree.getArray(zParam.value, new ArrayList<String>().getClass()));
                json.put(key, JsonTree.getJSONArray(zParam.value));
            }else{
                json.put(key,zParam.value);
            }
        }
        return json.toJSONString();
    }


    public static void saveCid(String pushId) {
        if (TextUtils.isEmpty(pushId))return;
        ShareDB.Key.update(IAutoParams.kSec, IAutoParams.kPushId, pushId);
        HttpIFCtx ctx = ZGlobalMgr.getGlobalObj(Key);
        if (ctx != null) {
            ctx.pushId = pushId;
        }
    }

}
