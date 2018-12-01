package com.k12app.bean.acc;

/**
 * 优惠券
 */
public class CouponBean {
//    coupon_id": "xxx",
//            " coupon_scene":"xxx",
//            " coupon_amount": "xxx",
//            " coupon_type": "xxx",
//            "start_date": "xxx",
//            "end_date": "xxx",
//            "from": "xxx"

    public String coupon_id;
    public int coupon_scene;//优惠券使用课程类型 见2.3.4课程类型定义
    public double coupon_amount;//优惠券面额
    public String coupon_type;//1-代金券 2-折扣券
    public String start_date;//生效日期(yyyy-mm-dd)
    public String end_date;//失效日期(yyyy-mm-dd)
    public String from;//优惠券来源

}
