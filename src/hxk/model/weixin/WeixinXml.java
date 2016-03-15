package hxk.model.weixin;

import hxk.util.MD5;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * Created by xusongqin on 6/13/15.
 */
public class WeixinXml {
    private static Logger logger = Logger.getLogger(WeixinXml.class);

    /**
     * @description 将list参数转换为xml格式并且带签名的xml格式	
     * @param params
     * @return
     *2016-3-15  下午9:45:32
     *返回类型:String
     */
    public static String genXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><xml>");
        String packageSign = getPackageSign(params, sb, sb2);
        sb2.append("<sign>");
        sb2.append(packageSign);
        sb2.append("</sign>");
        sb2.append("</xml>");

        try {
            return new String(sb2.toString().getBytes(), "ISO8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @description 构造签名	
     * @param params
     * @return
     *2016-3-15  下午9:44:02
     *返回类型:String
     */
    private static String getPackageSign(List<NameValuePair> params,StringBuilder sb, StringBuilder sb2) {
	for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
            sb2.append("<" + params.get(i).getName() + ">");
            sb2.append(params.get(i).getValue());
            sb2.append("</" + params.get(i).getName() + ">");
        }
        sb.append("key=");
        sb.append(Constans.private_key);
        String packageSign = null;
        packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
	return packageSign;
    }

    /**
     * @description 按照字典顺序算出签名	
     *2015-7-2  上午10:05:27
     *返回类型:String
     */
    public static String genProductArgs(Constans constans) {
        List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
        packageParams.add(new BasicNameValuePair("appid", Constans.APPID));
        packageParams.add(new BasicNameValuePair("attach", constans.getAttach()));
        packageParams.add(new BasicNameValuePair("body", constans.getBody()));
        packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
        packageParams.add(new BasicNameValuePair("mch_id", Constans.mch_id));
        packageParams.add(new BasicNameValuePair("nonce_str",constans.getNonce_str()));
        packageParams.add(new BasicNameValuePair("notify_url", constans.getNotify_url()));
        //这里的判断条件是必须的..因为要按照字典顺序排序.这里是openid应该排在这里
        if(!StringUtils.isEmpty(constans.getOpenid())){
            packageParams.add(new BasicNameValuePair("openid",constans.getOpenid()));
        }
        packageParams.add(new BasicNameValuePair("out_trade_no",constans.getOut_trade_no()));
        packageParams.add(new BasicNameValuePair("spbill_create_ip","14.145.53.0"));
        packageParams.add(new BasicNameValuePair("total_fee", constans.getTotal_fee()));
        if(!StringUtils.isEmpty(constans.getOpenid())){
            packageParams.add(new BasicNameValuePair("trade_type","JSAPI"));
        }
        else {
            packageParams.add(new BasicNameValuePair("trade_type","NATIVE"));
	}
        
        return genXml(packageParams);
    }

    
    /**
     * @description 获取微信返回的内容	
     * @param constans
     * @return
     * @throws Exception
     *2016-3-15  下午9:38:23
     *返回类型:String
     */
    public static String getValue(Constans constans) throws Exception{
        HttpClient httpclient = new DefaultHttpClient();
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HttpPost post = new HttpPost(url);
        String xml = genProductArgs(constans);
        
        StringEntity myEntity = new StringEntity(xml, "utf-8");
        
        post.addHeader("Content-Type", "text/xml");
        post.setEntity(myEntity);
        HttpResponse response = httpclient.execute(post);
        HttpEntity resEntity = response.getEntity();
        InputStreamReader reader = new InputStreamReader(resEntity.getContent(), "utf-8");
        char[] buff = new char[1024];
        int length = 0;
        String str = null;
        while ((length = reader.read(buff)) != -1) {
           str = (new String(buff, 0, length));
        }
        httpclient.getConnectionManager().shutdown();
        return str;
    }
    
    /**
     * @description 获取微信支付链接	
     * @param userId 用户ID
     * @param fee 费用
     * @param type 微信返回链接有两种 : 一种是二维码链接(code_url),一种是预支付账单id(prepay_id)
     * @param openId 用户使用JSAPI使用时需要传openId
     *2016-3-15  上午9:45:03
     *返回类型:String
     */
    public static String getWeixinUrl(String userId,String fee,String type,String openId) {
	Element element = null;
	try {
	    Constans constans = new Constans();
	    constans.setTotal_fee(fee);
	    constans.setOut_trade_no(getTradeNo(fee));
	    constans.setAttach(userId);
	    if(!StringUtils.isEmpty(openId)){
		constans.setOpenid(openId);
	    }
	    String text = getValue(constans);
	    logger.info(text);
	    Document document = DocumentHelper.parseText(text);
	    Element root = document.getRootElement();
	    element = root.element(type);
	    
	} catch (DocumentException e) {
	    logger.info("DOM　: "+e.getMessage());
	    e.printStackTrace();
	} catch (Exception e) {
	    logger.info("e : " + e.getMessage());
	    e.printStackTrace();
	}
	return element.getText();
    }
    
    
    /*
     * 生成商品序号
     */
    private static String getTradeNo(String fee){
	long time = new Date().getTime();
	String pre = time + fee;
	int size = 32 - pre.length();
	StringBuilder builder = new StringBuilder();
	for (int i = 0; i < size; i++) {
	    builder.append("0");
	}
	
	String s = pre + builder.toString();
	return s;
    }
    
    
    
}
