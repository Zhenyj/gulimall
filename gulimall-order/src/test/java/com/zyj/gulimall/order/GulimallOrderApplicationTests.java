package com.zyj.gulimall.order;

import cn.hutool.core.util.IdUtil;
import com.zyj.gulimall.order.entity.OrderEntity;
import com.zyj.gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    OrderService orderService;


    @Test
    void testSnowFlake() {
        long l = IdUtil.getSnowflake().nextId();
        System.out.println(l);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void textSelect() {
        List<OrderEntity> list = orderService.list();
        //list.forEach((item)->{
        //    System.out.println(item);
        //});
        list.forEach(System.out::println);
    }

}
