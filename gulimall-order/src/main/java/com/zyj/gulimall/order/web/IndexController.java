package com.zyj.gulimall.order.web;

import cn.hutool.core.util.IdUtil;
import com.zyj.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author lulx
 * @date 2022-01-26 14:08
 **/
@Controller
public class IndexController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/test/createOrder")
    public String createOrderTest() {
        OrderEntity order = new OrderEntity();
        order.setOrderSn(IdUtil.fastSimpleUUID());
        order.setCreateTime(new Date());
        rabbitTemplate.convertAndSend("order.event.exchange", "order.create.order", order);
        return "ok";
    }

    @GetMapping("/{page}.html")
    public String toPage(@PathVariable("page") String page) {
        return page;
    }


}
