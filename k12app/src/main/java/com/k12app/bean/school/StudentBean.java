package com.k12app.bean.school;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 *学生信息
 */
public class StudentBean implements Serializable {
//    "student_info":{
//        " student_head_img_url":"xxx",
//                " student_name":"xxx",
//                " student_no":"xxx",
//                "area":"xxx",
//    " city":"xxx",
//                " school":"xxx",
//                " grade":"xxx",
//                " class":"xxx"
//    }

    public String student_id;
    public String student_head_img_url;
    public String student_name;
    public String student_no;
    public String student_grade;
    public String area;
    public String city;
    public String school;
    public String grade;
    @JSONField(name="class")
    public String myClass;

}
