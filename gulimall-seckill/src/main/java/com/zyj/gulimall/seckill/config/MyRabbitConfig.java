package com.zyj.gulimall.seckill.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Configuration;

/**
 * @author lulx
 * @date 2022-03-02 23:41
 **/
@Configuration
public class MyRabbitConfig {
//    @Autowired
//    RabbitTemplate rabbitTemplate;

    MyRabbitConfig(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {

        });

        /**
         * 消息正确抵达队列进行回调
         */
        rabbitTemplate.setReturnsCallback((ReturnedMessage returned) -> {

        });
    }

    //    @Bean
//    public MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

    //    @PostConstruct
//    public void initRabbitTemplate() {
//        /**
//         * 设置确认回调
//         * 服务收到消息就回调
//         */
//        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
//
//        });
//
//        /**
//         * 消息正确抵达队列进行回调
//         */
//        rabbitTemplate.setReturnsCallback((ReturnedMessage returned) -> {
//
//        });
//    }
}
