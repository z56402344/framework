package com.k12app.db.table;

import android.database.sqlite.SQLiteDatabase;

import com.k12app.common.GlobaleParms;

import z.db.BaseDBHelper;

// 学堂的数据库
public interface ISchoolTable {

    // 表名
    String TNAME = GlobaleParms.TABLE_XT;
    //学堂的ID
    String XT_ID = "id";
    //学堂的Bean
    String XT_BEAN = "bean";
    //是否下载过 0=没有下载,1=下载过
    String XT_ISDOWN = "isdown";
    //学堂的扩展字段
    String XT_EXTEND = "extend";
    //插入数据库时间
    String XT_CREATE_TIME = "create_time";

    class Table implements BaseDBHelper.ILTable {
        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuffer sb = new StringBuffer(128);
            sb.append("create table ").append(TNAME).append('(');
            sb.append(XT_ID).append(" text default \"\", ");
            sb.append(XT_BEAN).append(" text default \"\", ");
            sb.append(XT_ISDOWN).append(" int default 0, ");
            sb.append(XT_CREATE_TIME).append(" text default \"\", ");
            sb.append(XT_EXTEND).append(" text default \"\")");
            db.execSQL(sb.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
