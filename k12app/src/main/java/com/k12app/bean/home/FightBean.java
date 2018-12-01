package com.k12app.bean.home;

import com.k12app.bean.subscribe.TeacherBean;

/**
 * 拼课的bean
 */
public class FightBean {
    public TeacherBean teacher_info;
    public FightInfo fight_course_info;

    public class FightInfo{
//        " course_id":"xxx",
//                " course_time":"xxx",
//                "course_question":"xxx",
//                " fight_course_fee":"xxx"
        public String course_id;
        public String course_time;
        public String course_question;
        public String fight_course_fee;

    }
}
