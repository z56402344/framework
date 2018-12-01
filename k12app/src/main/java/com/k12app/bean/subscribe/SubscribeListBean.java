package com.k12app.bean.subscribe;

import com.k12app.bean.school.StudentBean;

import java.io.Serializable;
import java.util.ArrayList;

//预约问集合
public class SubscribeListBean {

//    "total_num": "xxx",
//            "yyw_list": [
//    {
//        "yyw_info": {
//        " course_id": "xxx",
//                "course_type": "xxx",
//                " course_time": "xxx",
//                " status": "xxx",
//                "appoint_status": "xxx"
//    },
//        "teacher_info": {
//                "teacher_id": "xxx",
//                "teacher_cert_type": "xxx",
//                " teacher _head_img_url": "xxx",
//                " teacher _name": "xxx",
//                " teacher _stars": "xxx",
//                " teacher _no": "xxx",
//                "period ": "xxx",
//                "subject ": "xxx",
//                " fee_scale": "xxx",
//                "attent_status": "xxx"
//    }
//    }
//    ]
//            "live_info":{
//        " live_id":"xxx",
//                " live_user_id":"xxx",
//                "live_user_token":"xxx"
//    }


    public int total_num;//
    public ArrayList<SubList> yyw_list;

    public static class SubList implements Serializable{
       public SubInfoBean yyw_info;
       public TeacherBean teacher_info;
       public LiveBean live_info;
       public StudentBean student_info;
    }

}
