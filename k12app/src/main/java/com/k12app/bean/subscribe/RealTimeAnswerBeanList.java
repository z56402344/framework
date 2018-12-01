package com.k12app.bean.subscribe;

import com.k12app.bean.acc.RealTimeBean;
import com.k12app.bean.school.StudentBean;

import java.util.ArrayList;

//即时答列表Bean
public class RealTimeAnswerBeanList {

//    "type":"xxx",
//            "already_grab_result":{
//        "jsw_info":{
//            "course_id":"xxx",
//                    " title":"xxx",
//                    " content":"xxx",
//                    " img_list":[
//            {
//                "img_url":"xxx"
//            }
//            ],
//            "status":"xxx"
//        },
//        "student_info":{
//            " student _head_img_url":"xxx",
//                    " student _name":"xxx",
//                    " student _no":"xxx",
//                    " student_grade":"xxx"
//        },
//        "live_info":{
//            " live_id":"xxx",
//                    " live_user_id":"xxx",
//                    "live_user_token":"xxx"
//        }
//    },
//            "can_grab_result": [{
//        "jsw_info":{
//            "course_id":"xxx",
//                    " title":"xxx",
//                    " content":"xxx",
//                    " img_list":[
//            {
//                "img_url":"xxx"
//            }
//            ]
//        },
//        "student_info":{
//            " student _head_img_url":"xxx",
//                    " student _name":"xxx",
//                    " student _no":"xxx",
//                    " student_grade":"xxx"
//        }
//    }]

    public int type;//0-存在抢单成功待处理的即时答 1-未抢单
    public RealTimeAnswerBean already_grab_result;//抢单成功待处理的即时答
    public ArrayList<RealTimeAnswerBean> can_grab_result;//可抢单即时答列表

    public class RealTimeAnswerBean {
        public RealTimeBean.RealTimeInfo jsw_info;
        public StudentBean student_info;
        public LiveBean live_info;
    }


}
