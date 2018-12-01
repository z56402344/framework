package com.k12app.core;

import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.k12app.common.GlobaleParms;
import com.k12app.db.dao.IUser;

import java.io.File;

import z.frame.Util;

public class FileCenter {
	public static final String TsF = ".nomedia";
	// 是否存在sd卡
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static File getRoot(){
		File root = hasSDCard() ? Environment.getExternalStorageDirectory()
				: MainApplication.getApplication().getFilesDir();
		root = new File(root, "k12");
		if (!root.exists()) {
			root.mkdirs();
		}
		return root;
	}

	// 获取存取根目录
	public static File getRootDir() {
		return new File(getRoot(), GlobaleParms.isTeacher?"teacher":"student");
	}

	// 隐藏目录中的文件 不被音乐播放器等搜索到
	public static void hideAllMedia() {
		File file = new File(getRootDir(),TsF);
		if (!file.exists()) {
			Util.writeText("k12", file.getAbsolutePath());
		}
	}
	//获取用户展示页面的相册目录
	public static File getPhotoDir() {
		return new File(getRootDir(), "tempImage");
	}
	// 获取根目录路径
	public static String getRootPath() {
		return getRootDir().getAbsolutePath();
	}

	// 获取下载目录
	public static File getDownloadDir() {
		return new File(getRootDir(), "download");
	}
	public static String getDownloadPath() {
		return getDownloadDir().getAbsolutePath();
	}
	public static File getDFile(String file) {
		return new File(getDownloadDir(), file);
	}
	public static String getDPath(String file) {
		return getDFile(file).getAbsolutePath();
	}

	// 获取课堂目录
	public static File getSchoolDir() {
		return new File(getRootDir(), "school");
	}
	public static File getSchoolFile(String file) {
		return new File(getSchoolDir(), file);
	}


	// 获取课程路径
	public static File getAllTaskDir() {
		return new File(getRootDir(), "task");
	}
	public static String getAllLessonPath() {
		return getAllTaskDir().getAbsolutePath();
	}
	public static File getTaskDir(String lessonid) {
		return getTaskDir(getAllTaskDir(), lessonid);
	}
	public static File getTaskDir(File root, String lessonid) {
		return new File(root, lessonid);
	}
	public static String getTaskPath(String lessonid) {
		return getTaskDir(lessonid).getAbsolutePath();
	}

	// 获取用户所在目录 和id相关
	public static File getUserDir() {
		return new File(getUserRootDir(), IUser.Dao.getUserId());
	}

	// 获取用户所在根目录
	public static File getUserRootDir() {
		return new File(getRootDir(), "users");
	}

	//用户设置交流区背景图File
	public static File getMsgBgFile(String file) {
		return new File(getUserDir(), file);
	}

	public static String getUserPath() {
		return getUserDir().getAbsolutePath();
	}

	// 获取Log路径
	public static File getLogDir() {
		return new File(getRootDir(), "log");
	}
	public static String getLogPath() {
		return getLogDir().getAbsolutePath();
	}
	public static File getLogFile(String fName) {
		return new File(getLogDir(), fName);
	}
	public static String getLogFilePath(String fName) {
		return getLogFile(fName).getAbsolutePath();
	}


	public static File getCacheFileDir() {
		return new File(getRootDir(), "cacheFile");
	}

	public static File getVideoDir() {
		return new File(getRootDir(), "video");
	}

	private static final String Sep = "_";

	// 创建id 逗号分隔 方便拆解还原
	public static String buildSrcID(String src) {
		return src;
	}

	// 对应Apk
	public static String buildApkID(String apkVer) {
		return buildSrcID(TApk + Sep + apkVer);
	}

	public static String buildID(String id) {
		return buildSrcID(TMP3 + Sep + id );
	}

	public static final int TApk = 0;
	public static final int TMP3 = 1;
	public static final int TMP4 = 2;

	public static String getJsonKey(String json,String key, String def) {
		if (json==null) return def;
		try {
			JSONObject js = JSON.parseObject(json);
			key = js.getString(key);
			return key!=null?key:def;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	public static boolean getJsonKey(String json,String key, boolean def) {
		if (json==null) return def;
		try {
			JSONObject js = JSON.parseObject(json);
			return js.containsKey(key)?js.getBooleanValue(key):def;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	public static int getJsonKey(String json,String key, int def) {
		if (json==null) return def;
		try {
			JSONObject js = JSON.parseObject(json);
			return js.containsKey(key)?js.getIntValue(key):def;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	public static long getJsonKey(String json,String key, long def) {
		if (json==null) return def;
		try {
			JSONObject js = JSON.parseObject(json);
			return js.containsKey(key)?js.getLongValue(key):def;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	// k12/k12.cfg ==> {"url":"http://172.16.0.31/api","debug":true}
	public static String loadCfg() {
		String cfg = Util.readText(getRootPath()+"/k12.cfg", 2048);
		if (cfg==null) return null;
		cfg = cfg.trim();
		return cfg.length()>6?cfg:null;
	}
}
