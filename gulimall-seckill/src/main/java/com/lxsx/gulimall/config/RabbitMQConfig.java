package com.lxsx.gulimall.config;


import com.lxsx.gulimall.mq.MQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
     * 普通队列
     * @return
     */
    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(MQConstants.SECKILL_ORDER_QUEUE,true,false,false);
    }

    @Bean
    public Binding seckillOrderCreateBinding(){

        return   new Binding(MQConstants.SECKILL_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.ORDER_EVENT_EXCHANGE,
                MQConstants.SECKILL_ORDER_CREATE,
                null);
    }



}
