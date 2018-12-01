package com.k12app.bean.subscribe;

//微课堂
public class MicClassBean {

    //老师端
//    " course_id":"xxx",
    //        "course_name":"xxx",
    //        "cover_img_url":"xxx",
    //        "course_time":"xxx",
    //        " status":"xxx",
    //        "join_student":"xxx",
    //        " free_code":"xxx"
//    course_des

    public String course_id;//
    public String course_name;//学生端首页名称
    public String course_question;//老师端首页名称
    public String cover_img_url;//
    public String course_time;//
    public int course_type;
    public String course_des;
    public int status;//见2.3.5课程状态定义
    public String join_student;//报名人数
    public float fee_scale;//
    public int appoint_status;//见2.3.1约课状态定义 0未约;1已约;2已结束
    public String free_code;//免费码

}
