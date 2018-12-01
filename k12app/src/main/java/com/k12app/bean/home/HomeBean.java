package com.k12app.bean.home;

import java.util.ArrayList;

/**
 * 首页bean
 */
public class HomeBean {

//    "fight_course_num":"xxx",
//            "week_course_status":{
//               "three_day_before_today":"xxx",
//                "two_day_before_today":"xxx",
//                "one_day_before_today":"xxx",
//                "today":"xxx",
//                "one_day_after_today":"xxx",
//                "two_day_after_today":"xxx",
//                "three_day_after_today":"xxx"
//    },
//            "appoint_course_list":[
//    {
//            "teacher_id":"xxx",
//            "teacher_cert_type":"xxx",
//            " teacher_head_img_url":"xxx",
//            " teacher_name":"xxx",
//            " teacher_stars":"xxx",
//            " teacher_no":"xxx",
//            "period ":"xxx",
//            "subject ":"xxx",
//            " course_id":"xxx",
//            " course_time":"xxx",
//            " course_type":"xxx",
//            " status":"xxx"
//    "course_question":"xxx",
//    }


    public int fight_course_num;//当前可参与拼课的数量
    public ArrayList<WeekBean> week_course_status;//前后1周时间内约课记录状态
    public ArrayList<Course> appoint_course_list;//今日约课数组


    public static class Course {
        public String teacher_id;//老师id
        public int teacher_cert_type;//老师认证类型定义
        public String teacher_head_img_url;//老师头像地址
        public String teacher_name;//老师姓名
        public float teacher_stars;//老师星级
        public String teacher_no;//老师身份编号
        public String period;//学段
        public String subject;//科目定义
        public String course_id;//课程id(预约问课程编号/微课堂课程编号)
        public String course_time;//上课时间
        public int course_type;//课程类型定义 1即时问; 2 1对1预约问; 3拼课预约问; 4微课堂; 5学堂
        public String course_question;//课程题目

        //        0即时问类型:待抢单;预约问和微课堂类型:未开始
//        1即时问类型:已抢单;拼课预约问类型:拼课失败
//        2直播中
//        3已结束
//        4用户已取消
//        5老师已取消
        public int status;//课程状态定义


    }

}
