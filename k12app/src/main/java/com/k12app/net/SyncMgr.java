package com.k12app.net;

import java.util.Observable;
import java.util.Observer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import com.k12app.bean.AppVersionBean;
import com.k12app.common.ContantValue;
import com.k12app.common.GlobaleParms;
import com.k12app.common.IAutoParams;
import com.k12app.db.dao.IUser;
import com.k12app.db.table.SyncTable;
import com.k12lib.afast.log.Logger;

import z.db.DBQuery;
import z.db.ShareDB;
import z.ext.base.ZGlobalMgr;
import z.ext.frame.NotifyCenter;
import z.ext.frame.ZKeys;
import z.frame.NetLis;
import z.http.ZHttpCenter;

// 同步管理 (串化请求)
// 为防止数据重复计算,批量请求分2步走
// (1)批量打包,状态即可到1Done;(2)后续添加的记录不再计入这批次;(3)请求成功后再批量打包下一批次


// 数据库表
// id ts(时间戳) type(类型) ival(整数值) sval(字符串数据)
// v2.0 考虑关卡上传时间长问题

public class SyncMgr implements HttpItem.IOKLis, HttpItem.IErrLis, IKey, Observer {
    private static final int BadRecordTimeout = 86400 * 1000 * 30; // 30天未请求成功的数据
    public static final int ST_ZipDel = 10001; // 延时删除
    public static final int ST_Del = -1; // 删除
    public static final int ST_Init = 0; // 初始化状态
    public static final int ST_1Doing = 1; // 第1步执行中
    public static final int ST_1Done = 2; // 第1步完成
    public static final int ST_2Doing = 3; // 第2步执行中
    public static final String AC_APP = "app";

    public static final int Type_AppVersion = 7;
    public static final int Type_GetUserInfo = 8;//
    public static SyncMgr s_Instance = null;
    public static long mCurMills = 0;
    public static long s_LastTimeout = 0; // 超时等待
    public static final int TimeoutWait = 60 * 1000; // 超时后最少等待时间
    private static final int MaxRetry = 100; // 最大重传次数 (非网络原因)

    public static void httpTimeout(boolean bOut) {
        if (bOut) {
            s_LastTimeout = System.currentTimeMillis();
        } else {
            // 有请求成功了 清除超时等待
            s_LastTimeout = 0;
            onActive();
        }
    }

    public static class TaskLis implements HttpItem.IOKLis {
        private HttpItem.IOKLis mLis = null;
        private String mId;

        public TaskLis(HttpItem.IOKLis lis, String missionId) {
            mLis = lis;
            mId = missionId;
        }

        @Override
        public boolean onHttpOK(String msg, HttpResp resp) {
            resp.id = resp.id >> 16;
            switch (resp.id) {
            default:
                return true;
            }
//            if (mLis != null) {
//                mLis.onHttpOK(msg, resp);
//            }
//            return true;
        }
    }

    public HttpItem.IOKLis mLis = null;

    private Context mCtx;

    public static SyncMgr instance() {
        if (s_Instance == null) {
            s_Instance = new SyncMgr();
            s_Instance.mCtx = ZGlobalMgr.getGlobalObj(ZKeys.kAppContext);
            NotifyCenter.register(NetLis.Key, s_Instance);
        }
        return s_Instance;
    }

    public static void destroy() {
        if (s_Instance != null) {
            s_Instance.mHdl.removeCallbacks(s_Instance.mTask);
            NotifyCenter.unregister(NetLis.Key, s_Instance);
            s_Instance.mCtx = null;
            s_Instance = null;
        }
    }

    // 登录成功
    public static void handleLoginOK() {
        if (s_Instance == null) return;
        synchronized (s_Instance) {
            DBHelper dh = new DBHelper();
            // 重置状态
            dh.resetDoingStatus(ST_1Doing, ST_Init);
            dh.resetDoingStatus(ST_2Doing, ST_1Done);
            long timeout = (System.currentTimeMillis() - BadRecordTimeout);
            // 清除僵尸记录
            dh.deleteRecord(SyncTable.TimeStamp + '<' + timeout);
            dh.close();
            s_Instance.postCheck(20);
        }
    }
    //以后如果要加任何参考方法
    // 同步测试结果 批量
    //  refId:   'taskID'=>1,
    //  s1:   'answers'=> [{"id":"101", // 题目ID "pass":"1" // 回答正确 }, { "id":"102", "pass","0" //回答错误 }]
    //  i1:   'star'=>1
//    public static void addTestRes(String answers, int star, String taskID) {
//        if (s_Instance != null)
//            s_Instance.insertSyncRecord(null, Type_TestRes, ST_Init, star, 0, answers, null, taskID);
//    }

    public synchronized void insertSyncRecord(String ts, int tp, int st, int i1, int i2, String s1, String s2, String refId) {
        ContentValues cv = new ContentValues();
        if (ts == null) ts = String.valueOf(System.currentTimeMillis());
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        if (refId == null) refId = "";

        cv.put(SyncTable.TimeStamp, ts);
        cv.put(SyncTable.Type, tp);
        cv.put(SyncTable.Status, st);
        cv.put(SyncTable.V_I1, i1);
        cv.put(SyncTable.V_I2, i2);
        cv.put(SyncTable.V_S1, s1);
        cv.put(SyncTable.V_S2, s2);

        DBHelper dh = new DBHelper();
        dh.insertRecord(cv);
        dh.close();

        postCheck(1000);
    }

    private int mCurType = Type_AppVersion; // 下次检查项目
    // 循环任务
    private Handler mHdl = new Handler(Looper.getMainLooper());
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            int ms = checkCache();
            if (ms > 0) {
                postCheck(ms);
            }
        }
    };

    private static class DBHelper extends DBQuery {
        public boolean insertRecord(ContentValues cv) {
            try {
                if (!open()) return false;
                db.insert(SyncTable.TNAME, null, cv);
                return true;
            } catch (Exception e) {
                _log(e.toString());
            }
            return false;
        }

        public boolean updateStatus(String select, int status) {
            return updateStatus(select, status, -1);
        }

        public boolean updateStatus(String select, int status, int nFail) {
            try {
                if (!open()) return false;
                ContentValues cv = new ContentValues();
                cv.put(SyncTable.Status, status);
                if (nFail >= 0) {
                    cv.put(SyncTable.Retry, nFail);
                }
                db.update(SyncTable.TNAME, cv, select, null);
                return true;
            } catch (Exception e) {
                _log(e.toString());
            }
            return false;
        }

        public boolean resetDoingStatus(int from, int to) {
            try {
                if (!open()) return false;
                ContentValues cv = new ContentValues();
                cv.put(SyncTable.Status, to);
                db.update(SyncTable.TNAME, cv, SyncTable.Status + "=" + from, null);
                return true;
            } catch (Exception e) {
                _log(e.toString());
            }
            return false;
        }

        public boolean deleteRecord(String select) {
            try {
                if (!open()) return false;
                db.delete(SyncTable.TNAME, select, null);
                return true;
            } catch (Exception e) {
                _log(e.toString());
            }
            return false;
        }

        //		public boolean queryTypeStateOrder(String[] cols, int type, int state, boolean order) {
        public boolean queryTypeStateOrder(int type, int state) {
            StringBuffer sb = new StringBuffer(32);
            if (type > -1) {
                sb.append(SyncTable.Type).append('=').append(type).append(" and ");
            }
            sb.append(SyncTable.Status).append('=').append(state);
            return query(SyncTable.TNAME, null, sb.toString(), null, null, null, SyncTable.TimeStamp, null);
        }

        public boolean query(String[] cols, String select, String[] selectArgs, String groupBy, String having, String orderBy, String limit) {
            return query(SyncTable.TNAME, cols, select, selectArgs, groupBy, having, orderBy, limit);
        }
    }

    // 检查最后一次请求时间
    private boolean checkCancel() {
        if (mIds.length() > 0 && (mCurMills - mHttpTick < MaxReqTime)) {
            return false;
        }
        mHttpTick = mCurMills;
        return true;
    }

    // 放弃之前的操作
    private void cancelLast(DBHelper dh) {
        if (mIds.length() > 0) {
            ZHttpCenter.cancelAll(this);
            dh.updateStatus(mIds.toString(), mPreST);
            mIds.setLength(0);
        }
        if (mHttp != null) {
            mHttp.setListener(null);
            mHttp.setId(-1);
            mHttp = null;
        }
        ++mCurId;

        // 设置请求时间
        mHttp = new HttpItem();
        mHttp.setListener(this);
    }

    private int checkCache() {
        _log("check sync cache ===== ");
        if (!IUser.Dao.isLogin()) return 6000 * 1000;
        mCurMills = System.currentTimeMillis();
        // 当前有请求进行中 等待完成
        if (!checkCancel()) return 0;
        if (NetLis.getCurrentState(mCtx) == NetLis.None) return 60 * 1000;

        // 上次请求失败了 等一等
        if (mCurMills < s_LastTimeout + TimeoutWait)
            return (int) (s_LastTimeout + TimeoutWait - mCurMills);

        DBHelper dh = new DBHelper();
        cancelLast(dh);
        boolean bDone = false;
        int curType = mCurType;
        do {
            switch (curType) {
            }
        } while (curType != mCurType && !bDone);

        dh.close();
        if (bDone) {
            mCurType = curType;
            return 0;
        }
        _log("checkCache:no task!");
        return 600 * 1000; // 没任务 休息休息
    }

    private void commitIds(DBHelper dh, int type, int curST, int preST, int postST) {
        mHttp.setId((type << 16) | (mCurId & 0xFFFF));
        // "id in" + (ids)
        dh.updateStatus(mIds.toString(), curST);
        // 保存失败/成功后状态
        mPreST = preST;
        mPostST = postST;
    }

    private void setSimpleId(Cursor cursor) {
        mIds.setLength(0);
        mIds.append(" id=");
        mIds.append(cursor.getInt(cursor.getColumnIndex(SyncTable.ID)));
        mIds.append(' ');
    }

    // 先检查上批次是否成功 成功了再发下一批次
    private boolean checkBatEmpty(DBHelper dh, int type) {
        if (!dh.queryTypeStateOrder(type, ST_1Done)) {
            if (!dh.queryTypeStateOrder(type, ST_Init))
                return true;
        }
        return false;
    }


    // -1=del
    private void onResult(boolean bOK) {
        if (mIds.length() == 0) return;
        DBHelper dh = new DBHelper();
        int status = bOK ? mPostST : mPreST;
        boolean bNet = true;
        if (status == ST_Del) {
            dh.deleteRecord(mIds.toString()); // 成功 删除记录
        } else {
            bNet = NetLis.getCurrentState(mCtx) != NetLis.None;
            if (bNet) {
                // 有网 但是请求失败了 需要记录时间等一等
                if (s_LastTimeout == 0) {
                    mRetry += 1; // 如果处在失败状态下 不重复累计重试次数
                }
                httpTimeout(true);
                if (mRetry > MaxRetry) {
                    dh.deleteRecord(mIds.toString()); // 超出最大限制 删除记录
                } else {
                    dh.updateStatus(mIds.toString(), status, (mRetry)); // 失败 恢复记录
                }
            } else {
                dh.updateStatus(mIds.toString(), status); // 失败 恢复记录
            }
        }
        dh.close();

        mRetry = -2;
        mIds.setLength(0);
        // 触发下次执行
        // 成功了 立即执行下一条
        // 失败了 有网15s 没网30s
        postCheck(bOK ? 20 : (bNet ? 61 : 300) * 1000);
    }

    private final StringBuilder mIds = new StringBuilder(256);
    private int mRetry = 0;
    private int mCurId = 0;
    private int mPreST = 0; // 失败后还原状态
    private int mPostST = -1; // 完成后状态
    private HttpItem mHttp;
    private long mHttpTick = 0;
    private static final long MaxReqTime = 1800 * 1000; // 30 min

    @Override
    public boolean onHttpOK(String msg, HttpResp resp) {
        int type = resp.id >> 16;
        if (type == Type_AppVersion) {
            onAppVerRequest(resp);
            return true;
        } else if (type == Type_GetUserInfo) {
            return true;
        }
        // 已经不是本次请求
        if ((resp.id & 0xFFFF) != mCurId) return true;
        onResult(true);
        return true;
    }

    private static void _log(String txt) {
        Logger.w("SyncMgr", txt);
    }

    @Override
    public void onHttpError(int id, int errCode, String errMsg, Throwable e) {
        int type = id >> 16;
        if (type == Type_AppVersion) {
            mDaily = false;
            return;
        }
        // 已经不是本次请求
        if ((id & 0xFFFF) != mCurId) return;
        onResult(false);
    }

    // appVersion请求结果处理
    private void onAppVerRequest(HttpResp resp) {
        _log("Type_AppVersion >>> 获取app版本号");
        mLastDate = curDate();
        ShareDB.Key.update("version", "active_ts", mLastDate);
        AppVersionBean ver = resp.getObject(AppVersionBean.class, RES);
        if (ver != null) {
            ShareDB.Sec sec = new ShareDB.Sec(SyncMgr.AC_APP);
            sec.put("timediff", ver.timediff);
            sec.put("updatemode", ver.updatemode);
            sec.put("version", ver.version);
            sec.put("version_url", ver.version_url);
            sec.put("version_des", ver.version_des);
            sec.put("lastUp", SyncMgr.curSec());
            sec.save(false);

        }
        mDaily = false;
    }


    public static void onActive() {
        SyncMgr sm = instance();
        sm.checkActive();
    }

    private int mLastDate = 0;
    private boolean mDaily = false;

    // 换算到GMT+8时区的0点时间戳 +8小时
    public static int curDate() {
        return (int) ((System.currentTimeMillis() + 8 * 3600 * 1000) / 86400000);
    }

    public static int curSec() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private void checkActive() {
        postCheck(20);
        if (mDaily) return;

        if (mLastDate == 0) {
            ShareDB.Sec sec = new ShareDB.Sec("version");
            String curVer = IAutoParams.Sec.loadString(IAutoParams.kVerS);
            mLastDate = sec.getInt("active_ts");
            String lastVer = sec.getString("version");
            if (lastVer == null || !lastVer.equals(curVer)) {
                // 需要重新请求
                mLastDate = 1;
                sec.put("active_ts", 1);
                sec.put("version", curVer);
                sec.save(true);
            }
        }
        int date = curDate();
        if (mLastDate == date) {
            _log("checkActive >>" + " DailyRequest接口已请求过");
            return;
        }
        _log("checkActive >>" + " 需要重新请求DailyRequest接口");
        fireAppVersion();
    }

    public static void fireAppVersion() {
        SyncMgr sm = SyncMgr.instance();
        if (sm.mDaily) return;
        // 发起请求
        sm.mDaily = true;
        ShareDB.Sec sec = new ShareDB.Sec(IAutoParams.kSec);
        HttpItem httpItem = new HttpItem().setListener(sm).setId((Type_AppVersion << 16) | 0);
        httpItem.setUrl(ContantValue.F_CheckAppUpdate)
                .put("type", GlobaleParms.isTeacher?1:0)
                .put("version",sec.getString(IAutoParams.kVerS))
                .post(sm);
    }

    // 监听网络事件
    @Override
    public void update(Observable observable, Object o) {
        if (o != null && (o instanceof int[])) {
            int[] states = (int[]) o;
            if (states.length < 2) return;
            // 网络变更了
            int old = states[0];
            int cur = states[1];
            if (cur != NetLis.None) {
                postCheck(20);
            }
        }
    }

    private void postCheck(int ms) {
        mHdl.removeCallbacks(mTask);
        mHdl.postDelayed(mTask, ms);
    }

    public void setListener(HttpItem.IOKLis lis) {
        mLis = lis;
    }


    public static AppVersionBean getVer() {
        AppVersionBean ver = new AppVersionBean();
        ShareDB.Sec sec = new ShareDB.Sec(SyncMgr.AC_APP);
        ver.timediff = sec.getString("timediff");
        ver.updatemode = sec.getString("updatemode");
        ver.version = sec.getString("version");
        ver.version_url = sec.getString("version_url");
        ver.version_des = sec.getString("version_des");
        return ver;
    }

}
