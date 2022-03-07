package com.zyj.gulimall.order.config.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.zyj.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lulx
 * @date 2022-02-21 13:54
 **/
/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {


//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public String app_id = "2021000119617444";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAwAsOgMDpgLmRVJ6NdrvrFRZelPPUJCn1oe/df1APzDtS0DIZqu0wTlMni1XqJXYqfTBDs/zhINN/a8uwYBI1o+Mm1//AJ8D/7XwvmBhvR4kkIOHgOqeGsiQY9+IW/IlvhYj4GGjWVSqMtwixrm6A2+gAaSrGDikyAJv/lY1HKZNC/cpOiqDR2OusAWdqmCLVPBZrtVhHYM+svaHtTiUp9LCNTgzylpMT3Ph3FRzxpb3kLWHeYWbHkwFhlivmseCK4t0fV52J16njR6DDbRxJI+oorwhZL1LRgc1cNEeWvuEwwNIkYz8AYuSwIk+8x9g+8w151VC6vf+J+54sM6PxAgMBAAECggEAKnakeO94kHihaRkBTR0JTUL6CKA8D1JgRBMG6v8vPiu6LgZb4ze6xVecDiwuI0VZgQbPaTrvzREURxAScarYmCSg2F/hog2gAUHqh/mz+GAEIId1+5YwbgY02aNSlcnQ5Va/1BhckEt28kUojyxkf9Lyi5Sfrb5U6QUlWOszkqd9z8lEGuYxNslKH4SuOD86acPk6hAIKSaiciX1PmdAyqg2uEQtmV3rbCn4bF058oN8r2X7wwYN6WYW85duAqES//WyR4gCjaKKKaeAEzG+hC8MM7UmCjVSwdQ+8r4cPEkePlsDcKlLNTFvrSQgmAGzk1peztRAdfopW4wz4K6o2QKBgQD0JN1GxEpFUvdtL4XytzssbGnW4QNL4CtVUbxvY29w5MPQQjZVJYf40NyGKrmS61Oq0MF5J/1lyjXXeK8NZSwqUeIcafl+z2oxgWgNHoM+GKVvcsHYEZj7rUy6Rs8cSKTx3suRMQUEvml5IrvWAvldmq8LL+0LfSOBfTvPSHah3wKBgQDKHEONXC5XOQwabwclLa5oMHd9LOrRsU9xeeKImS5CeXGtHPnllynSyWDAM8nkLHPHEK3ZBTnSr6GZX1bLxak3QB5S5XwDUopE2kXRg1WSMhj69pXQlERxCzBYJV6piu4YLoJcTC/ztxgCJiyJCwJeCmWo1ROnLrvft5zjaAmULwKBgQDgGw67LOd4JyZUJXFxUNySdJna0wVxmr5sPPEZ1k6vdG6CNqUlF6Rus1oaFhHuWPGeNK3EBv9Fdo5h0bfuNdaKqktCBc0tqDs0YjMfB8k1MO57FP321oB1Vai+vMzUi6JNG1QFXNfceOTcexOs6WH5mEnhnQacqyvyawCBz2SGRQKBgQC74M5DCB/j4RoJMJDZ1L8+B1/sMucOit8ow378MDk6fh5YKIl9DapJUf3obbwNqezubejNxvfbjHySUHgk8Ne+jpIhyUYg6Ie0PZuNiaLwRjXGhZb6z/lXhGkqZ4K55HY0cHeqSQCCc/+Tb90Sf/NxbATYj0q3rM/kxZDILJxK7QKBgFKohouACQLLB0KxUMYtayXYRzhEqqvcNStyO9AlvU49ZAre5tPewBEPYWY6bxG55Jwn+dX62fJJA154GZnLjzgkRxNiWHrapIciu61KEgL86ApqygEvVs6Z+m9/iiEX5+mGt88rodTqICXur3xFgkkZZhp+Ia+MFfSW8VBQvb8S";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApw5ur0UKNC7KPNknLQ0bI0GBD3RqCm6dvubNRBieVNscxIjGzKBBkHl0tEZkJZ/TgtVmaw60VR69PNtkNFcvZNKhRr0w+Bz0yylXSE2UpebXT7YFIllvtAZo3L4VWyzWVsCM4xr95mW0KceSuUwkk8LGNgPWI0SDN/jSpv72BcSs8Mtw2xS0ac8IaghXmOMG3K5ND4d6ztUoAHCLT+JJjpeQViEWyr0OQMp2GxkejcfuWuLQxAroEznnA7BgvzoebVz4wFmo2/sSwqcO4KjPUfH57rNS7BMw1QN811RYsHIT7/e0f2M8aVcnEn24K5fjxdY7OwQSmotBcdXrrCxJ2QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public String notify_url = "http://2bxi93.natappfree.cc/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    public String sign_type = "RSA2";

    // 字符编码格式
    public String charset = "utf-8";

    // 支付宝网关
    public String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public String log_path = "C:\\";

    private String timeout = "29m";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    public String pay(PayVo vo) throws AlipayApiException {
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        // System.out.println("支付宝的响应："+result);

        return result;

    }
}
