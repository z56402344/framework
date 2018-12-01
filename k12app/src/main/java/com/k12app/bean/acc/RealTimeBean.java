package com.k12app.bean.acc;

import com.k12app.bean.subscribe.ImgBean;
import com.k12app.bean.subscribe.LiveBean;

import java.util.ArrayList;

/**
 * 即时问Bean
 */
public class RealTimeBean {
//    " jsw_total_num ":"xxx",
//      "jsw_list":[{
//              "jsw_info":{
//                    "course_id":"xxx",
//                    " title":"xxx",
//                    "content":"xxx",
//                    "img_list":[{
//                    "img_url":"xxx"
//            }],
//                    "period ":"xxx",
//                    "subject ":"xxx",
//                    "publish_time":"xxx",
//                    "end_time":"xxx",
//                    "status":"xxx"

//        },
//        "live_info":{
//            " live_id":"xxx",
//                    " live_user_id":"xxx"
//        }
//    }]



    public int jsw_total_num;//
    public int exist_flag;//是否有即时问 0-有 1-没有
    public ArrayList<RealTimeList> jsw_list;//

    public static class RealTimeList{
        public RealTimeInfo jsw_info;
        public LiveBean live_info;//即时问直播json数据
    }

    public static class RealTimeInfo {
        public String course_id;//即时问课程编号
        public String title;//即时问题目
        public String content;//即时问内容
        public ArrayList<ImgBean> img_list;//即时问图片数字
        public String period;//学段
        public int subject;//科目定义
        public String user_id;//对方的id,老师/学生
        public String publish_time;//发布时间
        public String end_time;//直播结束时间
        public long timestamp_publish;//发布时间
        public long timestamp_start;//开始时间
        public int status = -1;//课程状态定义
        //    0 即时问类型:待抢单 /预约问和微课堂类型:未开始
        //    1即时问类型:已抢单 /拼课预约问类型:拼课失败
        //    2 直播中
        //    3 已结束
        //    4 用户已取消
        //    5 老师已取消

    }

}

