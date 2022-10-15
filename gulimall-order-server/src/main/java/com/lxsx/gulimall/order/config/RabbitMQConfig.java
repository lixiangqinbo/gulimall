package com.lxsx.gulimall.order.config;


import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.lxsx.gulimall.order.listener.OrderMsgConfirmCallbackService;
import com.lxsx.gulimall.order.listener.OrderMsgReturnCallbackService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    /**
     * 配置Rabbit的回调
     */
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 依赖注入 rabbitTemplate 之后再设置它的回调对象
     * 设置回调函数
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(new OrderMsgConfirmCallbackService());
        rabbitTemplate.setReturnCallback(new OrderMsgReturnCallbackService());
    }
    /**
     * 配置RabbitMQ的消息序列化机制 JSON
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstants.ORDER_EVENT_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstants.ORDER_RELEASE_ORDER);
        arguments.put("x-message-ttl", MQConstants.MESSAGE_TTL); // 消息过期时间 1分钟

        return new Queue(MQConstants.ORDER_DELAY_QUEUE,true,false,false,arguments);
    }

    /**
     * 普通队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue(){
        return new Queue(MQConstants.ORDER_RELEASE_ORDER_QUEUE,true,false,false);
    }

    /**
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){

        return new TopicExchange(MQConstants.ORDER_EVENT_EXCHANGE,
                true,
                false);
    }

    @Bean
    public Binding orderCreateOrderBinding(){

      return   new Binding(MQConstants.ORDER_DELAY_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.ORDER_EVENT_EXCHANGE,
                MQConstants.ORDER_CREATE_ORDER,
                null);
    }
    @Bean
    public Binding orderReleaseOrderBinding(){

        return   new Binding(MQConstants.ORDER_RELEASE_ORDER_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.ORDER_EVENT_EXCHANGE,
                MQConstants.ORDER_RELEASE_ORDER,
                null);
    }

    @Bean
    public Binding orderReleaseOtherBinding(){

        return   new Binding(MQConstants.STOCK_RELEASE_STOCK_QUEUE,
                Binding.DestinationType.QUEUE,
                MQConstants.ORDER_EVENT_EXCHANGE,
                MQConstants.STOCK_RELEASE_OTHER,
                null);
    }



}
