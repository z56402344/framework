package com.k12app.bean.subscribe;

//预约问单独的bean
public class SubInfoBean {


//    "course_id":"xxx",
    //        "course_type":"xxx",
    //        " course_question":"xxx",
    //        " status":"xxx",
    //        "student_id":"xxx",
    //        " student_head_img_url":"xxx",
    //        " student_name":"xxx",
    //        " student_no":"xxx",
    //        " student_grade":"xxx"


    public String course_id;//预约问id(仅当查询我的预约问时返回)
    public int course_type;//见2.3.4课程类型定义(仅当查询我的预约问时返回)
    public String course_time;//开课时间(仅当查询我的预约问时返回)
    public int status;//见2.3.5课程状态定义(仅当查询我的预约问时返回)
    public int appoint_status;//见2.3.1约课状态定义
    public String course_question;//课程问题
    public String student_id;//
    public String student_head_img_url;//
    public String student_name;//
    public String student_no;//
    public String student_grade;//

}
