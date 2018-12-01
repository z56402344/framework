package com.k12app.bean.subscribe;

import com.k12app.bean.acc.RealTimeBean;

/**
 * 我的最新即时问bean
 */
public class NewRealTime {
    public int exist_flag;//是否有即时问 0-有 1-没有
    public RealTimeBean.RealTimeInfo jsw_info;
    public LiveBean live_info;//即时问直播json数据
}
