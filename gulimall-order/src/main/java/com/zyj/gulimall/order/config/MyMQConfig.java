package com.zyj.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置
 *
 * @author lulx
 * @date 2022-02-15 14:57
 **/

@Slf4j
@Configuration
public class MyMQConfig {

    public static final Long DELAY_QUEUE_MESSAGE_TTL = 60000L;
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_RELEASE_ORDER_ROUTING_KEY = "order.release.order";
    public static final String ORDER_create_ORDER_ROUTING_KEY = "order.create.order";
    public static final String ORDER_EVENT_EXCHANGE = "order.event.exchange";

    /**
     * 监听队列，当有消费者监听队列时，才会自动创建相关的队列
     */
//    @RabbitListener(queues = ORDER_RELEASE_ORDER_QUEUE)
//    public void listener(OrderEntity entity) {
//        log.info("订单过期:" + entity.getOrderSn());
//    }

    /**
     * 死信队列，队列设置过期时间，通过死信路由到指定的队列上，
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", ORDER_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key", ORDER_RELEASE_ORDER_ROUTING_KEY);
        args.put("x-message-ttl", DELAY_QUEUE_MESSAGE_TTL);
        Queue queue = new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue(ORDER_RELEASE_ORDER_QUEUE, true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange() {
        TopicExchange exchange = new TopicExchange(ORDER_EVENT_EXCHANGE, true, false);
        return exchange;
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        Binding binding = new Binding(ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                ORDER_EVENT_EXCHANGE,
                ORDER_create_ORDER_ROUTING_KEY,
                null);
        return binding;
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        Binding binding = new Binding(ORDER_RELEASE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                ORDER_EVENT_EXCHANGE,
                ORDER_RELEASE_ORDER_ROUTING_KEY,
                null);
        return binding;
    }

    /**
     * 订单释放与库存释放进行绑定
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        Binding binding = new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                ORDER_EVENT_EXCHANGE,
                "order.release.other.#",
                null);
        return binding;
    }

    /**
     * 秒杀订单队列
     *
     * @return
     */
    @Bean
    public Queue orderSeckillOrderQueue() {
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Binding orderSeckillOrderQueueBinding() {
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order.event.exchange",
                "order.seckill.order",
                null);
    }
}
