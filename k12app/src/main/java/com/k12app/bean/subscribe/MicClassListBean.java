package com.k12app.bean.subscribe;

import java.util.ArrayList;

/**
 * 微课堂
 */
public class MicClassListBean {
//    "total_num":"xxx",
//            "wkt_list":[{
//        "wkt_info":{
//            " course_id":"xxx",
//                    "course_name":"xxx",
//                    "cover_img_url":"xxx",
//                    "course_time":"xxx",
//                    " status":"xxx",
//                    "join_student":"xxx",
//                    " fee_scale":"xxx",
//                    "appoint_status":"xxx"
//        },
//        "teacher_info":{
//            "teacher_id":"xxx",
//                    "teacher_cert_type":"xxx",
//                    " teacher _head_img_url":"xxx",
//                    " teacher _name":"xxx",
//                    " teacher _stars":"xxx",
//                    " teacher _no":"xxx",
//                    "period ":"xxx",
//                    "subject ":"xxx",
//                    "attent_status":"xxx",
//        }

    public int total_num;//
    public ArrayList<MicClassListBean.MicClassList> wkt_list;

    public static class MicClassList{
        public MicClassBean wkt_info;
        public TeacherBean teacher_info;
        public LiveBean live_info;
    }

}
