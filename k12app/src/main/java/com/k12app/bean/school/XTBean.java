package com.k12app.bean.school;

import com.k12app.bean.subscribe.TeacherBean;

import java.io.Serializable;

/**
 * 点击学堂播放按钮返回的bean
 */
public class XTBean implements Serializable {
//    "xt_info":{
//        "name":"xxx",

//                "des ":"xxx",
//                "play_url":"xxx",
//                "allow_reward":"xxx"
//    },
//            "teacher_info":{
//        " teacher _id":"xxx",
//                "teacher_cert_type":"xxx",
//                " teacher _head_img_url":"xxx",
//                " teacher _name":"xxx",
//                " teacher _stars":"xxx",
//                " teacher _no":"xxx",
//                "period ":"xxx",
//                "subject ":"xxx",
//                "intro":"xxx"
//    },
//            "student_info":{
//        " student _head_img_url":"xxx",
//                " student _name":"xxx",
//                " student _no":"xxx",
//                " city":"xxx",
//                " school":"xxx",
//                " grade":"xxx",
//                " class":"xxx"
//    }
    public XTInfo xt_info;
    public TeacherBean teacher_info;
    public StudentBean student_info;

    public static class XTInfo implements Serializable {
        public String name;
        public String course_id;
        public int course_type;
        public String des;
        public String play_url;
        public String allow_reward;

    }
}
