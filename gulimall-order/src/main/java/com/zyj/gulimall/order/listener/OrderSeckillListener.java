package com.zyj.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.zyj.common.to.mq.SeckillOrderTo;
import com.zyj.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lulx
 * @date 2022-03-04 13:47
 **/
@Slf4j
@RabbitListener(queues = {"order.seckill.order.queue"})
@Component
public class OrderSeckillListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo orderTo, Channel channel, Message message) throws IOException {
        int [][] arr = new int[10][10];
        int [] arr1[] = new int[10][10];
        int  arr2 [][] = new int[10][10];
        log.info("监听到秒杀订单:{}", orderTo.getOrderSn());
        try {
            orderService.createSeckillOrder(orderTo);
            MessageProperties messageProperties = message.getMessageProperties();
            channel.basicAck(messageProperties.getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
