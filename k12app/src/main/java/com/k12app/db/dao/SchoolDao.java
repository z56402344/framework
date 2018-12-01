package com.k12app.db.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.k12app.bean.school.SchoolBean;
import com.k12app.db.table.ISchoolTable;
import com.k12app.net.SyncMgr;
import com.k12lib.afast.log.Logger;

import java.util.ArrayList;

import z.db.BaseDBHelper;
import z.db.DBQuery;
import z.db.DU;
import z.frame.JsonTree;

/**
 * 说明：消息的表的操作
 */
public class SchoolDao implements ISchoolTable {
    public static final String TAG = SchoolDao.class.getSimpleName();
    public static final int XT_DOWN = 1;//已下载
    public static final int XT_NOTDOWN = 0;//未下载


    //向数据库增加一条下载记录
    public static void addXT(SchoolBean xtBean) {
        SchoolBean.CourseInfo courseInfo = xtBean.xt_info;
        if (xtBean == null || courseInfo == null) {
            return;
        }

        SQLiteDatabase db = BaseDBHelper.getWDatabase();
        ContentValues cv = new ContentValues();

        try {
            SchoolBean old = querySchoolBean(courseInfo.course_id);
            bean2Db(xtBean, cv);
            if (old != null) {
                db.update(TNAME, cv, XT_ID + "=?", new String[]{courseInfo.course_id});
            } else {
                db.insert(TNAME, null, cv);
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
    }

    private static void bean2Db(SchoolBean xtBean, ContentValues cv) {
        cv.put(XT_ID, xtBean.xt_info.course_id);
        cv.put(XT_BEAN, JsonTree.toJSONString(xtBean));
        cv.put(XT_ISDOWN, XT_NOTDOWN);
        cv.put(XT_CREATE_TIME, SyncMgr.curSec());
        cv.put(XT_EXTEND, "");
    }

    // 根据courseId查询
    public static SchoolBean querySchoolBean(String courseId) {
        SchoolBean bean = null;
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, XT_ID + "=?", new String[]{courseId}, null, null, null, null)) {
            prepareDb2Bean(dbq);
            bean = db2Bean(dbq);
        }
        dbq.close();
        return bean;
    }

    private static void prepareDb2Bean(DBQuery dbq) {
        dbq.prepareCols(4)
                .addCol(XT_ID)
                .addCol(XT_BEAN)
                .addCol(XT_ISDOWN)
                .addCol(XT_EXTEND);
    }

    private static SchoolBean db2Bean(DBQuery dbq) {
        String json = dbq.getString(1);
        SchoolBean schoolBean = JsonTree.getObject(json,SchoolBean.class);
        schoolBean.is_down = dbq.getInt(2);
        schoolBean.extend = dbq.getString(3);
        return schoolBean;
    }

    //更新对应课程是否下载 默认0=未下载，1=已下载
    public static void updateIsDown(String courseId) {
        DU.Once.updateAttr(true, TNAME, XT_ID, courseId, XT_ISDOWN, XT_DOWN);
    }

    //查询数据库所有支付数据
    public static ArrayList<SchoolBean> queryAll() {
        ArrayList<SchoolBean> list = new ArrayList<SchoolBean>();
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, null, null, null, null, XT_CREATE_TIME + " DESC",
                null)) {
            prepareDb2Bean(dbq);
            do {
                list.add(db2Bean(dbq));
            } while (dbq.next());
        }
        dbq.close();
        return list;
    }

    //查询数据库所有下载的数据
    public static ArrayList<SchoolBean> queryDownAll() {
        ArrayList<SchoolBean> list = new ArrayList<SchoolBean>();
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, null, XT_ISDOWN + "=1", null, null, null, XT_CREATE_TIME + " DESC",
                null)) {
            prepareDb2Bean(dbq);
            do {
                list.add(db2Bean(dbq));
            } while (dbq.next());
        }
        dbq.close();
        return list;
    }

    // 查询下载的数量
    public static int queryUnDownCount() {
        int count = 0;
        DBQuery dbq = new DBQuery();
        if (dbq.query(TNAME, new String[]{"count(*)"}, XT_ISDOWN + "=1", null, null, null, null, null)) {
            count = dbq.getInt("count(*)");
        }
        dbq.close();
        return count;
    }


}
