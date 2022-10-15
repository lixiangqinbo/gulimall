package com.lxsx.gulimall.order.listener;

import com.lxsx.gulimall.order.enume.OrderStatusEnum;
import com.lxsx.gulimall.order.service.OrderMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * orderMsg的消息的确认回调
 * 消息子只要被broker接收就会执行这个回调，如果是集群要所有的broker都收到才会调用回调
 *     这个回调这能说明broker收到了生产者的消息，不代表投递成功
 */
@Slf4j
@Component
public class OrderMsgConfirmCallbackService implements RabbitTemplate.ConfirmCallback {
    @Resource
    private OrderMsgService orderMsgService;

    /**
     * correlationData
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String orderSn = correlationData.getId();
        if (!ack) {
            log.error("消息发送异常! correlationData={} ,ack={}, cause={}", correlationData.getId(), ack, cause);

            orderMsgService.updateStatus(orderSn,cause, OrderStatusEnum.OrderMsgStatusEnum.MQ_DELIVERY_FAIL.getCode());
        }else {
            log.info("消息发送成功");
            orderMsgService.updateStatus(orderSn,cause, OrderStatusEnum.OrderMsgStatusEnum.MQ_DELIVERY_SUCCESS.getCode());
        }
    }
}
