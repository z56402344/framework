package com.k12app.service;

/**
* Created by i51talk on 15-11-11.
*/
public interface ICmd {
	String Key = "cmdid";
	String Opt = "opt";
	String ID = "id";
	String LinkUrl = "link_url";

	//服务启动的类型
	/** 下载应用APK*/
	int DOWN_APK = 1;
	//推送的Push
	int Push = 2;
	// 点击通知消息
	int NotifyClick = 3;


	// 推送类型
	String KPushT = "t";
	int ToH5 = 1;
	//管理员发布活动赛事
	int ToActivityTea = 110;
	int ToActivityStu = 111;
	//优惠券赠送
	int ToCouponTea = 120;
	int ToCouponStu = 121;
	//打赏通知
	int ToReward = 130;

	//即时问
	//学生发布完即时问
	int ToRepltimeTea = 210;
	//老师抢单即时问
	int ToRepltimeStu = 221;
	//学生选择即时问辅导老师
	int ToSelecteTeacherTea = 230;
	//给未被选的辅导老师发送推送
	int NotToSelecteTeacherTea = 231;
	//学生结束辅导,推送通知老师端结束辅导
	int ToTeacherEndCoaching = 240;
	//即时问强制关闭消息推送
	int ToStuCloseRealtime = 250;
	//即时问强制关闭消息推送
	int ToTeacherCloseRealtime = 251;

	//预约问
	//学生发布完1对1预约问
	int To1V1Tea = 310;
	//学生取消1对1预约问
	int ToCancel1V1Tea = 320;
	//老师取消1对1预约问
	int ToCancel1V1Stu = 331;
	//学生发布完拼课预约问
	int ToFightCourseStu = 341;
	//预约问拼课成功
	int ToFightCourseSuccessTea = 350;
	int ToFightCourseSuccessStu = 351;
	//拼课成功后老师取消预约问
	int ToFightCourseCancelStu = 361;
	//拼课成功后老师取消预约问
	int ToFightCourseCancelTea = 370;
	//预约问课前通知
	int ToBeforeClassTea = 380;
	int ToBeforeClassStu = 381;

	//微课堂
	//老师取消微课堂
	int ToMirClassCancelStu = 411;
	//微课堂课前通知
	int ToMirBeforeClassTea = 420;
	int ToMirBeforeClassStu = 421;

	// 开关配置项 sec
	String SwiSec = "msg_switch";
	String IsFirst = "isfirst";

}
