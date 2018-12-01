package com.k12app.common;


import com.k12app.core.FileCenter;

/** 说明：全局的变量 */
public class GlobaleParms {
	/**  创建数据库所有表的名称 */
	public static  String TABLE_USERINFO = "userInfo";
	public static  String TABLE_MSG = "msg";
	public static  String TABLE_XT = "xt";
	public static  boolean isTeacher = false;//false=学生版 true=教师版 0-学生版 1-教师版
	public static  int QueryNum = 20;//控制所有分页查询，单次查询量

	public static String[] CourseType ={"即时问","1对1预约问","拼课预约问","微课堂","学堂"};

	public static String[] UmengAppKey = {"5947358a8f4a9d2f33000630","59473611f29d984e350000b9"};
	public static String[] UmengSecret = {"e42a59c6bf098f72defdf4fc2dd3f022","7428a09495e94cb408597d576d012afb"};



	// coderoom.cfg ==> {"url":"http://","debug":true}
	// 调试开关
	public static final boolean isDebug = FileCenter.getJsonKey(FileCenter.loadCfg(), "debug", true);
//	public static final boolean isDebug = true;

	/** 线上环境*/
//	public static final String BaseURL = "http://gateway.ke12.cn";
	/** 预上线环境*/
//	public static final String BaseURL = "http://";
	/** 测试线环境 */
//	public static final String BaseURL = "http://47.94.96.183:12080";
	public static String BaseURL = FileCenter.getJsonKey(FileCenter.loadCfg(), "url", "http://gateway.ke12.cn");

	public static final String URL = BaseURL + "" ;

}
