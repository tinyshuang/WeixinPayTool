package hxk.util;

import hxk.model.weixin.Constans;
import hxk.model.weixin.WeixinXml;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


/**
 * @author Administrator
 * @description 微信支付工具
 *2016-3-14  下午10:21:49
 */
public class WeixinTool {
    
    /**
     * @description 微信支付生成二维码的图片的接口	
     * @param userId 回调回来用来确定是谁支付的
     * @param fee 费用
     * @return 返回一个图片IO流
     *2016-3-14  下午10:25:49
     *返回类型:BufferedImage
     */
    public static BufferedImage pay(String userId){
	//TODO fee定义的是商品的价格,之后写项目引入时改下这里的价格
	String fee = "1";
	String url = WeixinXml.getWeixinUrl(userId,fee,"code_url","");
	BufferedImage image = QRCodeUtil.qRCodeCommon(url, "PNG", 20);
	return image;
    }
    
    
    /**
     * @description 手机支付模式	
     * @param userId 回调回来用来确定是谁支付的
     * @param openid
     * @return
     *2016-3-14  下午10:31:45
     *返回类型:Map<String,String>
     */
    public static Map<String, String> mobilePay(String userId,String openid){
	//TODO fee定义的是商品的价格,之后写项目引入时改下这里的价格
	String fee = "1";
	String url = WeixinXml.getWeixinUrl(userId,fee,"prepay_id",openid);
	List<NameValuePair> params = createPackageParams(url);
	params.add(new BasicNameValuePair("paySign", getPackageSign(params)));
	Map<String, String> resultMap = new LinkedHashMap<String, String>();
	for (NameValuePair nameValuePair : params) {
	    resultMap.put(nameValuePair.getName(), nameValuePair.getValue());
	}
	return resultMap;
	
    }
    
    
    /**
     * @description 构造签名	
     * @param params
     * @return
     *2016-3-14  下午10:41:12
     *返回类型:String
     */
    private static String getPackageSign(List<NameValuePair> params) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
	sb.append("key=" + Constans.private_key);
	String packageSign = null;
        packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
	return packageSign;
    }
    
    
    /**
     * @description 构造手机微信浏览器支付时参数值对	
     * @param url 
     * @return
     *2016-3-14  下午10:39:59
     *返回类型:List<NameValuePair>
     */
    private static List<NameValuePair> createPackageParams(String url){
	List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appId", Constans.APPID));
        packageParams.add(new BasicNameValuePair("nonceStr", Constans.genNonceStr()));
        packageParams.add(new BasicNameValuePair("package", "prepay_id="+url));
        packageParams.add(new BasicNameValuePair("signType", "MD5"));
        packageParams.add(new BasicNameValuePair("timeStamp", String.valueOf(Math.round(System.currentTimeMillis() / 1000))));
        return packageParams;
    }
    
    

}
