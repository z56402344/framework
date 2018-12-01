package com.k12app.bean.school;

/**
 * Tab学堂首页
 */
public class SchoolBean {
    //    "xt_info":{
//        "course_id":"xxx",
//                "content_type":"xxx",
//                "name":"xxx",
//                "cover_img_url":"xxx",
//                "zan_num":"xxx",
//                "download_fee":"xxx"
//    },
//            "owner_info":{
//        "owner_id":"xxx",
//                "owner_head_img_url":"xxx",
//                "owner_name":"xxx",
//                "owner_no":"xxx",
//                "teacher_fan_num":"xxx"
//    }
    public CourseInfo xt_info;
    public OwnerInfo owner_info;

    //本地数据库信息
    public int is_down;//0==没有下载成功,1==下载成功
    public String extend;

    public static class CourseInfo {
        public String course_id;
        public int content_type;//学堂课程内容分类 0-视频 1-音频
        public String name;
        public String cover_img_url;
        public String zan_num;
        public String create_time;//课程创建时间
        public float download_fee;//下载费用
    }

    public static class OwnerInfo {
        public String owner_id;//
        public String owner_head_img_url;//
        public String owner_name;//
        public String owner_no;//
        public String teacher_fan_num;//

    }
}
