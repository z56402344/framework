package com.k12app.core;

import com.k12lib.afast.log.Logger;
import com.k12lib.afast.utils.IntentUtils;

import z.down.AsynFile;
import z.down.DMgr;
import z.down.IDown;
import z.frame.ICommon;

// 下载相关的
public class DownWrapper implements IDown {

    public static final String TsF = ".nomedia";

    // 下载apk
    public static String downApk(String apkVer, String url) {
        apkVer = FileCenter.buildApkID(apkVer);
        Download(apkVer, url, FileCenter.TApk, new ApkHandler());
        return apkVer;
    }

    //普通文件下载
    //任意id都可以，只是表示下载的唯一值
    public static String downMP3File(String id, String url) {
        id = FileCenter.buildID(id);
        DownloadSchool(id, url, FileCenter.TMP3, new NormalHandler());
        return id;
    }

    public static String downMP4File(String id, String url) {
        id = FileCenter.buildID(id);
        DownloadSchool(id, url, FileCenter.TMP4, new NormalHandler());
        return id;
    }


    private static String getSuffix4Type(int type) {
        switch (type) {
            case FileCenter.TApk:
                return ".apk";
            case FileCenter.TMP3:
                return ".mp3";
            case FileCenter.TMP4:
                return ".mp4";
        }
        return ".dat";
    }

    // 通常下载
    public static void Download(String id, String url, int type, IPostHandler ip) {
        DMgr.Download(id, url, FileCenter.getDFile(id + getSuffix4Type(type)), ip);
    }
    // 学堂下载
    public static void DownloadSchool(String id, String url, int type, IPostHandler ip) {
        DMgr.Download(id, url, FileCenter.getSchoolFile(id + getSuffix4Type(type)), ip);
    }

    // 安装包安装
    public static class ApkHandler extends RenameHandler implements ICommon {
        @Override
        public boolean onPostDownload(AsynFile task, boolean bOK) {
            super.onPostDownload(task, bOK);
            if (!bOK) return false;
            Logger.i("FileCenter", "新版本下载成功");
            // 安装新的apk包
            IntentUtils.installApk(app.ctx, task.mRealPath);
            return true;
        }
    }

    // 普通文件
    public static class NormalHandler extends RenameHandler implements ICommon {
        @Override
        public boolean onPostDownload(AsynFile task, boolean bOK) {
            super.onPostDownload(task, bOK);
            if (!bOK) return false;
            Logger.i("FileCenter", "下载成功");
            return true;
        }
    }
}
