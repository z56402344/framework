package com.k12app.bean.acc;

//是否可以提现
public class CanCash {
//    " status":"xxx",
//            " cash_money":"xxx",
//            " qr_code_url":"xxx"
    public int status;//0-可提现  1-不可提现(金额为0) 2-不可提现(未绑定微信公众号)
    public double cash_money;//提现金额
    public String qr_code_url;//关注微信公众平台的二维码地址 当status为2时返回,
    public String need_recharge_money;//需要充值的金额(元为单位)当status为2时返回,供app打开充值页面
    public String need_pkg_id;//需要充值的金额(元为单位)当status为2时返回,供app打开充值页面
}
