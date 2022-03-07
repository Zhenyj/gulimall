package com.zyj.gulimall.ware.listener;

import com.rabbitmq.client.Channel;
import com.zyj.common.to.mq.StockLockedTo;
import com.zyj.common.to.OrderTo;
import com.zyj.gulimall.ware.config.MyRabbitConfig;
import com.zyj.gulimall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author lulx
 * @date 2022-02-16 19:59
 **/
@Slf4j
@Service
@RabbitListener(queues = MyRabbitConfig.STOCK_RELEASE_STOCK_QUEUE)
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 库存自动解锁
     *
     * @param to
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try {
            log.info("库存工作单:{}，解锁库存", to.getId());
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error(e.getMessage());
            //消息拒绝以后,重新入队让其他继续解锁
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo order, Message message, Channel channel) throws IOException {
        try {
            log.info("订单关闭准备解锁库存：{}", order.getOrderSn());
            wareSkuService.unlockStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
