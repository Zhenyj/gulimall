package com.zyj.gulimall.product;

import com.zyj.gulimall.product.entity.BrandEntity;
import com.zyj.gulimall.product.service.BrandService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    //@Resource
    //OSSClient ossClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void testStringRedisTemplate() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 保存
        ops.set("hello", "world" + UUID.randomUUID().toString());
        // 查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据是:" + hello);
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功");
    }

    @Test
    void textUpdate() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setBrandId(1L);
        brandEntity.setDescript("华为P50");
        brandService.updateById(brandEntity);
    }


    @Test
    void commonsTest() {
        System.out.println(RandomUtils.nextInt(101));

        // 生成 0 ~ Double.MAX_VALUE 之间的双精度数值
        System.out.println(RandomUtils.nextDouble());

        // 生成10位随机字母的字符串
        System.out.println(RandomStringUtils.randomAlphabetic(10));

        // 生成10位随机字母或数字的字符串
        System.out.println(RandomStringUtils.randomAlphanumeric(10));

        // a,b,c 用于字符串拼接
        System.out.println(StringUtils.join(new String[]{"a", "b", "c"}, ","));

        // 00015 左补齐， 生成格式化单号时可能使用
        System.out.println(StringUtils.leftPad("15", 5));
        System.out.println(StringUtils.leftPad("15", 5, "0"));

        // true 判断是否包含某段字符串
        System.out.println(StringUtils.containsIgnoreCase("abc", "A"));

        // "a"
        System.out.println(StringUtils.substringAfterLast("abcba", "b"));

        // 前后去空格但可以规避 NPE 空指针异常
        System.out.println(StringUtils.trimToEmpty(null));
        System.out.println(StringUtils.trimToEmpty("    abc    "));


    }


}
