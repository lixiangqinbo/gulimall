package com.lxsx.gulimall.config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


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
     * 普通队列
     * @return
     */
    @Bean
    public Queue testQueue(){
        return new Queue("test.queue",true,false,false);
    }

    @Bean
    public Exchange testExchange(){

        return new TopicExchange("test_exchange", true, false);
    }

    @Bean
    public Binding testBinding(){

        return   new Binding("test.queue",
                Binding.DestinationType.QUEUE,
                "test_exchange",
               "test.queue.test",
                null);
    }



}
