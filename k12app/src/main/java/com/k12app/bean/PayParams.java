package com.k12app.bean;


import com.alibaba.fastjson.annotation.JSONField;

public class PayParams {
    public PayParams() {
    }

    public String payType;
    public String payId;
    public WxPayParams wxPayParams;
    public AliPayParams aliPayParams;
    public String error;


    /**
     * 说明：微信支付需要的参数
     *
     * @author 51talk
     * @version 创建时间：2016-3-31  下午3:18:46
     */
    public static class WxPayParams {

//        "appid":"xxx",
//                "partnerid":"xxx",
//                "prepayid":"xxx",
//                "package":"xxx",
//                "noncestr":"xxx",
//                "timestamp":"xxx",
//                "sign":"xxx"


        public String appid;
        public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
        public String sign;
        @JSONField(name="package")
        public String packageValue;

    }


    /**
     * 说明：支付宝支付所需要的数据
     *
     * @author 51talk
     * @version 创建时间：2016-3-31  下午3:19:37
     */
    public static class AliPayParams {

        public String app_id;//客户端号
        public String service;//接口名称
        public String partner;//合作者身份ID
        public String _input_charset;//参数编码字符集
        public String sign_type;//签名方式 目前仅支持RSA
        public String sign;//签名
        public String notify_url;//服务器异步通知页面路径
        public String out_trade_no;//商户网站唯一订单号
        public String subject;//商品名称
        public String payment_type;//支付类型
        public String seller_id;//卖家支付宝账号
        public String total_fee;//总金额 Number
        public String body;//商品详情
        public String it_b_pay;//未付款交易的超时时间
        public String alipay_str;//已经拼好的加密了的字符串 主要包含商户的订单信息，key=“value”形式，以&连接。
        public String appenv;//客户端来源
        public String goodsType;//商品类型
        public String rnCheck;//是否发起实名校验
        public String externToken;//授权令牌
        public String outContext;//商户业务扩展参数

    }


}
