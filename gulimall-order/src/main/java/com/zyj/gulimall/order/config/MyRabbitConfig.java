package com.zyj.gulimall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author lulx
 * @date 2022-01-25 15:36
 **/
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 消息转换器
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义RabbitTemplate的一些配置
     * 1、RabbitMQ的消息确认机制-可靠抵达
     */
    @PostConstruct // MyRabbitConfig对象创建完成以后，执行该方法
    public void initRabbitTemplate() {
        /**
         * 设置确认回调
         * 服务收到消息就回调
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 封装当前消息的唯一关联数据(包括消息的唯一id)
             * @param ack 消息是否成功收到，服务器成功接收消息即为成功
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // TODO 消息确认处理
            }
        });

        /**
         * 消息正确抵达队列进行回调
         */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * ReturnedMessage属性：
             *      1、message：投递失败的消息详细信息
             *      2、replyCode：回复的状态码
             *      3、replyText：回复的文本内容
             *      4、exchange：当时这个消息发给哪个交换机
             *      5、routingKey：当时这个消息用哪个路由键
             *
             * 注意：交换机将消息存到队列时出现错误时才会触发该方法
             * @param returned
             */
            @Override
            public void returnedMessage(ReturnedMessage returned) {
            }
        });

        /**
         * 消费端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）。
         * 1、默认是自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除这个消息
         *      问题:
         *          客户端收到很多消息，自动回复给服务器ack，只有一个消息处理成功后客户端宕机了。发生消息丢失；
         *          配置spring.rabbitmq.listener.simple.acknowledge-mode=manual，修改成手动确认
         * 2、手动确认模式下如何确认成功处理消息
         *      消费端可以接收一个Channel对象,调用basicAck()，可以手动确认收到消息
         *      出现异常等情况可以调用basicNack(long deliveryTag,boolean multiple,boolean requeue)
         *          deliveryTag：消息标签
         *          multiple：是否批量拒收
         *          requeue：是否重新添加到消息队列中,true:重新添加到消息队列,false:丢弃消息
         *      或basicReject(long deliveryTag,boolean requeue)表示没有签收消息
         *
         */

    }
}
