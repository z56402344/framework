package com.k12app.bean.subscribe;

import java.io.Serializable;

/**
 * 选择辅导老师Bean
 */
public class TeacherBean implements Serializable {
//    teacher_id":"xxx",
//            "teacher_cert_type":"xxx",
//            " teacher_head_img_url":"xxx",
//            " teacher_name":"xxx",
//            " teacher_stars":"xxx",
//            " teacher_no":"xxx",
//            "period":"xxx",
//            "subject":"xxx",
//            "fee_scale":"xxx",
//            "attent_status":"xxx"
//            "zan_num":"xxx"

//            "comment_num":"xxx",
//            "area":"xxx",
//            "honor_cert":"xxx",
//            "intro":"xxx",
//            "zan_status":"xxx",

//    "pk_discount":"xxx",
//            "pk_fee_scale":"xxx",

    public String teacher_id;//老师id
    public int teacher_cert_type;//0普通;1专业;2特约
    public String teacher_head_img_url;//老师头像地址
    public String teacher_name;//老师姓名
    public int teacher_stars;//老师星级
    public String teacher_no;//老师编号
    public String period;//学段
    public int subject;//subject 1 语文 2 数学 3 英语 4 物理 5 化学 6 体育 7 美术 8 音乐
    public double fee_scale;//收费标准
    public int attent_status;//当前学生对老师的关注状态 0-未关注;1-已关注
    public int zan_num;//赞数
    public int zan_status;//当前学生对老师的点赞状态 0-未点赞，1-已点赞
    public String comment_num;//评论总数
    public String area;//所在地区(xx省xx市)
    public String honor_cert;//荣誉证书
    public String intro;//自我介绍
    public double pk_discount;//拼课收费费率
    public double pk_fee_scale;//拼课收费标准


}
