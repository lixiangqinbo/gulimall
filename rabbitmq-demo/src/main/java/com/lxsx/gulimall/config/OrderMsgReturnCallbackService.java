package com.lxsx.gulimall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMsgReturnCallbackService implements RabbitTemplate.ReturnCallback {
    /**
     * 消息递交队列失败就会执行这个回调
     * 消息(带有路由键routingKey)到达交换机，与交换机的所有绑定键进行匹配，匹配不到触发回调
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("Broker路由QUEUE失败:returnedMessage ===> replyCode={} ,replyText={} ,exchange={} ,routingKey={},message={}", replyCode, replyText, exchange, routingKey,message);
    }
}
