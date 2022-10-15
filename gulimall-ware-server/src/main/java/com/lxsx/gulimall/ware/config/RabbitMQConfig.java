package com.lxsx.gulimall.ware.config;


import com.lxsx.gulimall.mq.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMQConfig {
    /**
     * 配置RabbitMQ的消息序列化机制 JSON
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 以锁库存的 死信队列
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstants.STOCK_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstants.STOCK_RELEASE);
        arguments.put("x-message-ttl", MQConstants.STOCK_MESSAGE_TTL); // 消息过期时间 2分钟

        return new Queue(MQConstants.STOCK_DELAY_QUEUE,true,false,false,arguments);
    }

    /**
     * 释放库存的普通队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue(){

        return new Queue(MQConstants.STOCK_RELEASE_STOCK_QUEUE,true,false,false);
    }

    /**
     *
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){

        return new TopicExchange(MQConstants.STOCK_EVENT_EXCHANGE,
                true,
                false);
    }

    /**
     * 以锁库存的绑定
     * @return
     */
    @Bean
    public Binding stockCreateStockBinding(){

      return  new Binding(MQConstants.STOCK_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.STOCK_EVENT_EXCHANGE,
                MQConstants.STOCK_LOCKED,
                null);
    }

    /**
     * 解锁库存 绑定
     * @return
     */
    @Bean
    public Binding stockReleaseStockBinding(){

        return   new Binding(MQConstants.STOCK_RELEASE_STOCK_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.STOCK_EVENT_EXCHANGE,
                MQConstants.STOCK_RELEASE+".#",
                null);
    }
    /**
     * 扣减库存的队列
     */
    @Bean
    public Queue stockReduceqStockQueue(){

        return new Queue(MQConstants.STOCK_REDUCE_STOCK_QUEUE,
                true,
                false,
                false);
    }
    /**
     * 减少库存 绑定
     * @return
     */
    @Bean
    public Binding stockReduceStockBinding(){

        return   new Binding(MQConstants.STOCK_REDUCE_STOCK_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.STOCK_EVENT_EXCHANGE,
                MQConstants.STOCK_REDUCE,
                null);
    }

}
