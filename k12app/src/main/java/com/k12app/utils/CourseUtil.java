package com.k12app.utils;

/**
 * 科目工具类
 */
public class CourseUtil {
    public static String[] mSubject = {"语文","数学","外语","政治","地理","历史","物理","化学","生物","非遗","其他"};
//    科目排序为：语文、数学、外语、政治、地理、历史、物理、化学、生物、非遗、其他

    //(教师资格证) 0-普通待认证 1-专业 2-特约
    public static String[] mCreat = {"普通待认证","专业","特约"};

    public static String getSubject(int type){
        if (type-1 < 0 || type-1 >= mSubject.length)return "";
        return mSubject[type-1];
    }

    public static String getCreat(int type){
        if (type >= mCreat.length)return "";
        return mCreat[type];
    }

    public static String getCourseType(int type){
        switch (type){
            case 1:
                return "即时问";
            case 2:
                return "1对1预约问";
            case 3:
                return "拼课预约问";
            case 4:
                return "微课堂";
            case 5:
                return "学堂";
        }
        return "";
    }

    public static String getSex(int sex){
        String mSex = null;
        if (sex == 1){
            mSex = "男";
        }else if (sex == 2){
            mSex = "女";
        }else if (sex ==3){
            mSex = "保密";
        }
        return mSex;
    }

    public static String getPeriod(String gradeStr) {
        StringBuilder sb = new StringBuilder();
        int gradeInt = 1;
        try {
            gradeInt = Integer.parseInt(gradeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gradeInt < 7) {
            sb.append("小学").append(gradeInt);
        } else if (gradeInt < 10) {
            sb.append("初中").append(gradeInt-6);
        } else {
            sb.append("高中").append(gradeInt-9);
        }
        return sb.append("年级").toString();
    }
}
