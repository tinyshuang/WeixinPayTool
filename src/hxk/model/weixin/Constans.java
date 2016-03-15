package hxk.model.weixin;

import hxk.util.MD5;

import java.util.Random;


/**
 * 请求需要的参数
 * @author Administrator
 *
 */
public class Constans {
    	//请求地址
    	public static final String URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
    	//key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
    	public static final String private_key = "###";
    	//公众账号ID
	public static final String APPID = "###";
	//商户号
	public static final String mch_id = "###";
	
	//APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	public static final String spbill_create_ip = "##";
	//商品描述
	private String body = "##";
	//设备号
	private String device_info = "WEB";
	//随机字符串
	private String nonce_str = genNonceStr();
	//商户订单号
	private String out_trade_no = genNonceStr();
	//回调地址
	private String notify_url = "http://###/recevie.do";
	//费用
	private String total_fee;
	private String attach;
	private String openid;
	
	
	public String getBody() {
	    return body;
	}
	public void setBody(String body) {
	    this.body = body;
	}
	public String getDevice_info() {
	    return device_info;
	}
	public void setDevice_info(String device_info) {
	    this.device_info = device_info;
	}
	public String getNonce_str() {
	    return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
	    this.nonce_str = nonce_str;
	}
	public String getOut_trade_no() {
	    return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
	    this.out_trade_no = out_trade_no;
	}
	public String getNotify_url() {
	    return notify_url;
	}
	public void setNotify_url(String notify_url) {
	    this.notify_url = notify_url;
	}
	public String getTotal_fee() {
	    return total_fee;
	}
	public void setTotal_fee(String total_fee) {
	    this.total_fee = total_fee;
	}
	//生成随机字符串
	public static String genNonceStr() {
	    Random random = new Random();
	    return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	public String getAttach() {
	    return attach;
	}
	public void setAttach(String attach) {
	    this.attach = attach;
	}
	public String getOpenid() {
	    return openid;
	}
	public void setOpenid(String openid) {
	    this.openid = openid;
	}
	
	
}
