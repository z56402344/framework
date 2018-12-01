package com.k12app.bean;

import java.util.ArrayList;

// db:[id] [type] [title] [iconUrl] [createTime][msgStatus][url][homeWorkId]
public class MsgListBean {

    public int totalPageNum;
    public int pageId;
    public int pageSize;
    public ArrayList<MsgBean> list;

    public static class MsgBean {
        public String mid;//消息id
        public String content;
        public String dateTime;//创建时间
        public String isRead;

    }

}
