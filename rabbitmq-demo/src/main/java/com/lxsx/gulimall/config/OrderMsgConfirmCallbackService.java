package com.lxsx.gulimall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * orderMsg的消息的确认回调
 * 消息子只要被broker接收就会执行这个回调，如果是集群要所有的broker都收到才会调用回调
 *     这个回调这能说明broker收到了生产者的消息，不代表投递成功
 */
@Slf4j
@Component
public class OrderMsgConfirmCallbackService implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            log.info("Broker消息接收失败! correlationData={} ,ack={}, cause={}", correlationData.getId(), ack, cause);
        }else {
            log.info("Broker消息接收成功correlationData={} ,ack={}, cause={}", correlationData.getId(), ack, cause);
        }
    }
}
