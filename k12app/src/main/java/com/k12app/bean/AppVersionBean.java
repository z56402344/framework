package com.k12app.bean;

/**
 * 版本升级信息
 */
public class AppVersionBean {
//    "timediff": "xxx",
//            "updatemode": "xxx",
//            "version": "xxx",
//            "version_des":"xxx",
//            "version_url":"xxx"
    public String timediff;//App本地时间与服务器时间的差值(精确到毫秒)
    public String updatemode;//升级模式 0:不需要升级; 1:需要升级; 2:强制升级
    public String version;
    public String version_des;
    public String version_url;

}
