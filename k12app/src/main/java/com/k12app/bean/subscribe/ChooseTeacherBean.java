package com.k12app.bean.subscribe;

//选择老师接口返回的Bean
public class ChooseTeacherBean {
//    "balance_not_enough":"xxx",
//            "live_info":{
//        " live_id":"xxx",
//                " live_user_id":"xxx",
//                "live_user_token":"xxx"
//    }
    public int balance_not_enough;//0=余额充足,1 ==余额不足
    public LiveBean live_info;
}
