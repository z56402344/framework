package com.k12app.bean.subscribe;

import java.io.Serializable;

//课程结束响应结果
public class CourseFinishBean implements Serializable{
//    " time":"xxx",
//    " fee_scale":"xxx",
//    " amount ":"xxx"

    public String time;//用时
    public String fee_scale;//收费标准
    public double amount;//学生本次收费
    public double income;//老师本次收入
    public int student_num;//老师端显示的上课人数

    public String teacherId;//老师ID  本地添加,非接口返回
    public String courseId;//课程ID  本地添加,非接口返回
    public int courseType;//课程类型ID  本地添加,非接口返回

}
