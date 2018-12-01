package com.k12app.common;


//接口地址
public interface ContantValue {
	String URL = GlobaleParms.URL + (GlobaleParms.isTeacher ? "/teacher" : "/student");
//	String URL = GlobaleParms.URL + "/teacher";
	String PublicURL = GlobaleParms.URL + "/public";

	//3.1 版本升级检查
	//用户启动APP后调用此接口进行版本升级检测及获取本地时间与服务器时间的差值(精确到毫秒)
	String F_CheckAppUpdate = PublicURL +"/checkAppUpdate";
	//3.2下发短信验证码
	String F_RegSms = PublicURL+ "/getCheckCode";
	//3.3	上传文件
	String F_UploadFile = PublicURL+ "/uploadFile";
	//3.5	查询充值套餐列表
	String F_RechargeList = PublicURL + "/queryRechargePkgList";
	//3.6	查询banner图列表
	String F_QueryBannerList = PublicURL + "/queryBannerList";//1 学生端首页;2 学生端老师学堂页;3 学生端学生学堂页;4 老师端首页;5 老师端老师学堂页 6 老师端学生学堂页
	//3.7	测试推送的接口
	String F_PushMessage = PublicURL + "/pushMessage";

	//登录注册相关
	//4.1.1 注册 */
	String F_Reg = URL + "/register";
	//4.1.2找回登录密码
	String F_GetPassword = URL+ "/getPassword";
	//4.1.3 登录
	String F_LOGIN = URL  +"/login";
	//4.1.4 登出
	String F_LOGOUT = URL +"/logout";
	//4.1.5 学生进入个人中心时调用此接口查询个人信息
	String F_Info = URL+ "/info";
	// 4.1.6 编辑个人资料
	String F_Edit = URL + "/edit";
	//4.1.7 修改登录密码
	String F_SETPWD = URL + "/modifyPassword";
	//4.1.8	查询我的钱包
	String F_QueryAccountInfo = URL + "/queryAccountInfo";
	//4.1.9 钱包--收入/支出列表
	String F_QueryAccountChangeList = URL + "/queryAccountChangeList";
	//4.1.10 充值
	String F_Recharge = URL + "/recharge";
	//4.1.11 设置交易密码
	String F_SetTradePwd = URL + "/setTradePwd";
	//4.1.12 找回交易密码
	String F_GetTradePwd = URL + "/getTradePwd";
	//4.1.13 修改交易密码
	String F_ModifyTradePwd = URL + "/modifyTradePwd";
	//4.1.14 我的优惠券
	String F_Coupon = URL + "/queryCouponList";
	//4.1.15 消息免打扰设置
	String F_DisturbSetting = URL + "/disturbSetting";
	//4.1.16 查询是否能提现
	String F_QueryCanCash = URL + "/queryCanCash";
	//4.1.17 确认提现
	String F_CashSubmit = URL + "/cashSubmit";


	//首页
	//4.2.1 查询首页数据
	String F_QueryIndexData = URL + "/queryIndexData";
	//4.2.2 查询可参与的拼课列表
	String F_QueryPkYywList = URL + "/queryPkYywList";
	//4.2.3 查询指定日期的已约课数据
	String F_QueryAppointCourseList = URL + "/queryAppointCourseList";
	//4.2.4 首页-取消已预约课程
	String F_CancelAppointCourse = URL + "/cancelAppointCourse";

	//约课
	//4.3.1 我的课堂 分页查询我的即时问列表
	String F_QueryMyJswList = URL + "/queryMyJswList";
	//4.3.2 查询我最新发布的即时问
	String F_QueryNewJswInfo = URL + "/queryNewJswInfo";
	//4.3.3 取消即时问
	String F_CancelJsw = URL + "/cancelJsw";
	//4.3.4 发布即时问
	String F_PublishJsw = URL + "/publishJsw";
	//4.3.5	查询即时问已接单老师人数  学生在发布完即时问后等待2分钟后调用此接口获取接单老师人数
	String F_QueryJswAcceptTeacherNum = URL + "/queryJswAcceptTeacherNum";
	//4.3.6 选择辅导老师列表
	String F_ChooseTeacher = URL + "/queryJswAcceptTeacherList";
	//4.3.7 选择某个辅导老师
	String F_SelectJswTeacher = URL + "/selectJswTeacher";
	//4.3.8 结束辅导  学生在即时问直播间点击结束辅导时调用此接口, 之后调用4.6.1场景支付接口完成支付
	String F_FinishTutor = URL + "/finishTutor";
	//4.3.9 分页查询预约问列表
	String F_QueryYywList = URL + "/queryYywList";
	//4.3.10 查询预约问详情
	//学生在进入预约问直播间时可调用此接口获取预约问详情
	String F_QueryYywInfo = URL + "/queryYywInfo";
	//4.3.11 发布预约问
	String F_PublishYyw = URL + "/publishYyw";
	//4.3.12 分页查询微课堂列表
	//学生在约课版块点击微课堂tab时调用此接口
	//学生在个人中心页面点击我的课堂微课堂tab时调用此接口
	String F_QueryWktList = URL + "/queryWktList";
	//4.3.12 查询微课堂详情
	//学生在进入微课堂直播间时可调用此接口获取微课堂详情
	String F_QueryWktInfo = URL + "/queryWktInfo";

	//4.4	学堂版块
	//4.4.1	分页查询学堂课程列表
	String F_QueryXtList = URL + "/queryXtList";
	//4.4.2 学堂课程播放
	String F_PlayXt = URL + "/playXt";
	//4.4.3 学堂课程点赞/取消点赞
	//学生在观看学堂课程时点击点赞/取消点赞时调用此接口
	String F_ZanXt = URL + "/zanXt";


	//4.5.1 查询我关注的老师列表
	String F_QueryMyAttentTeacherList = URL + "/queryMyAttentTeacherList";
	//4.5.2 查询老师详情
	//点击老师名称或头像查看老师介绍时进入老师介绍页面调用此接口
	String F_QueryTeacherInfo = URL + "/queryTeacherInfo";
	//4.5.3 给老师点赞/取消点赞
	String F_ZanTeacher = URL + "/zanTeacher";
	//4.5.4 查询老师评价列表
	String F_QueryTeacherCommentList = URL + "/queryTeacherCommentList";
	//4.5.5 给老师评价
	String F_CommentTeacher = URL + "/commentTeacher";
	//4.5.6 关注老师
	String F_AttentTeacher = URL + "/attentTeacher";

	//4.6.1 场景付费
	String F_Pay = URL + "/pay";
	//4.6.2 场景打赏 直播间点击打赏时调用此接口
	String F_Reward = URL + "/reward";

	//4.7 消息推送板块
	//4.7.1


	//5.1.16 查询即时问及预约问费用
	String F_QueryCourseFeeConfig = URL + "/queryCourseFeeConfig";
	//5.1.17 调整即时问及预约问费用
	String F_EditCourseFeeConfig = URL + "/editCourseFeeConfig";
	//5.1.18 查询忙闲时时段设定列表
	String F_QueryScheduleList = URL + "/queryScheduleList";
	//5.1.19 忙闲时时段设定
	String F_EditSchedule = URL + "/editSchedule";
	//5.1.20 发布微课堂
	String F_PublishWkt = URL + "/publishWkt";
	//5.1.22 我的预约问
	String F_QueryMyYywList = URL + "/queryMyYywList";
	//5.1.23 我的微课堂
	String F_QueryMyWktList = URL + "/queryMyWktList";

	//5.3.1 分页查询可抢单即时问列表 被5.3.4替代 废弃
	String F_QueryJswList = URL + "/queryJswList";
	//5.3.2 抢单
	String F_Grab = URL + "/grab";
	//5.3.3 抢单结果确认
	String F_GrabConfirm = URL + "/grabConfirm";
	//5.3.4 分页查询可抢单即时问列表NEW 5.3.1废弃
	String F_QueryJswListEx = URL + "/queryJswListEx";
	//5.3.5 查询即时答详情
	String F_QueryJswInfo = URL + "/queryJswInfo";
	//5.3.6 课堂打赏设置
	String F_RewardSetting = URL + "/rewardSetting";
	//5.3.7 开始辅导
	String F_StartTutor = URL + "/startTutor";
	//5.3.8 结束辅导 同 4.3.8 URL一样
//	String F_FinishTutor = URL + "/finishTutor";


	//5.4.4 学堂付费下载
	String F_payXt = URL + "/payXt";
	//5.4. 老师学堂打赏
	String F_RewardXt = URL + "/rewardXt";
	//5.4.6 学堂课程下载完成
	String F_FinishDownloadXt = URL + "/finishDownloadXt";

	//5.5 查询学生详情
	String F_QueryStudentInfo = URL + "/queryStudentInfo";

	//使用帮助
	String F_AppHelp = "http://www.ke12.cn/apphelp.html";
	//	服务协议和隐私条款
	String F_AppUserPrivacy = "http://www.ke12.cn/appuserprivacy.html";
	//关于我们
	String F_AppAboutus= "http://www.ke12.cn/appaboutus.html";
	//下载分享页面
	String F_AppShare =  "http://www.ke12.cn/androidshare.html?";
	//拼课分享页面
	String F_PKShare =  "http://www.ke12.cn/pinkeshare.html?";




	//确认支付
	String F_ConfirmPay = URL + "";
	/** 客服中心 */
	String F_FeedBack = URL + "/feedback/index/submitFeedBack";

    /** 获取消息 */
    String F_GET_MSG = URL +  "/message/index/getMessageList";



}
