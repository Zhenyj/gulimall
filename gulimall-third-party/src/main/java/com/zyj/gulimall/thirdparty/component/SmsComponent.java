package com.zyj.gulimall.thirdparty.component;

import com.zyj.gulimall.thirdparty.util.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lulx
 * @date 2022-01-19 16:03
 **/
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {

    private String host;
    private String path;
    private String method;
    private String appcode;

    public void sendSmsCode(String phone, String code) {
        // 开通服务后 买家中心-查看AppCode
        Map<String, String> headers = new HashMap<String, String>();
        // 最后在header中的格式(中间是英文空格)    // 为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", "【创信】你的验证码是：5873，3分钟内有效！");
        querys.put("mobile", "13685988379");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
