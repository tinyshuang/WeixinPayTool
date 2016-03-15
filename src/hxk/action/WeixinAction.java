package hxk.action;

import hxk.util.WeixinTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * @author Administrator
 * @description 微信支付action
 *2016-3-14  下午9:57:08
 */
@Controller
public class WeixinAction {
    private static Logger logger = Logger.getLogger(WeixinAction.class);
    private static final String RESUTL_XML = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    
    
    /**
     * @description 微信支付返回二维码给ＰＣ端	
     *2015-7-2  上午9:43:34
     *返回类型:void
     */
    @RequestMapping("pay")
    public void pay(HttpSession session,HttpServletResponse response){
	String userId = session.getAttribute("id").toString();
	try {
	    ImageIO.write(WeixinTool.pay(userId), "PNG", response.getOutputStream());
	} catch (IOException e) {
	    logger.info(e.getMessage(), e.fillInStackTrace());
	}
    }
    
    
    /**
     * @description 手机支付..就是微信浏览器支付模式..需要配合JSAPI来使用
     * 	参考链接 : https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=7_7&index=6	
     * @param session
     * @param num
     * @return
     *2016-3-15  下午9:48:06
     *返回类型:Map<String,String>
     */
    @RequestMapping("mpay")
    public @ResponseBody Map<String, String> mPay(HttpSession session){
	String userId = session.getAttribute("id").toString();
	//TODO 这里将openid从session或者数据库读出来
	String openId = "";
	Map<String, String> map= WeixinTool.mobilePay(userId,openId);
	return map;
    }
    
    
    
    
    /**
     * @description 微信支付的回调函数	
     *2015-7-4  下午3:10:06
     *返回类型:String
     */
    @RequestMapping("recevie")
    public @ResponseBody String receive(HttpServletRequest request){
	try {
	    InputStream in = request.getInputStream();
	    System.out.println(in);
	    //然后是读取微信回调的相关信息,记得保存相关数据
	} catch (IOException e) {
	    logger.info(e.getMessage(), e.fillInStackTrace());
	}
	//这里一定要返回以下xml字符串来告知微信我们已经收到回调
	return RESUTL_XML;
    }
    
}
