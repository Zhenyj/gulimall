package com.zyj.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.zyj.gulimall.thirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    void contextLoads() {
    }

    @Test
    public void testUpload() throws FileNotFoundException {
        //// Endpoint以杭州为例，其它Region请按实际情况填写。
        //String endpoint = "xxxxxxxxxxxxxxxxx";
        //// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
        //String accessKeyId = "xxxxxxxxxxxxxxxxx";
        //String accessKeySecret = "xxxxxxxxxxxxxxxxx";
        //
        //// 创建OSSClient实例。
        //OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流
        InputStream inputStream = new FileInputStream("F:\\images\\JAVA开发学习手册.png");
        // 上传文件到指定的存储空间（bucketName）并将其保存为指定的文件名称（objectName）。
        String content = "Hello OSS";
        ossClient.putObject("gulimall-zhenyj", "JAVA开发学习手册.png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功");
    }

    @Test
    public void testSms() {
        smsComponent.sendSmsCode("13685988379", "123456");
    }
}
