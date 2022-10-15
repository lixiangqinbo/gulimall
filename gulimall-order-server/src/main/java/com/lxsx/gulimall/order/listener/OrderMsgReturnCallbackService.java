package com.lxsx.gulimall.order.listener;

import com.lxsx.gulimall.order.enume.OrderStatusEnum;
import com.lxsx.gulimall.order.service.OrderMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息递交队列失败就会执行这个回调
 */
@Slf4j
@Component
public class OrderMsgReturnCallbackService implements RabbitTemplate.ReturnCallback {

    @Resource
    private OrderMsgService orderMsgService;
    /**
     * 消息路由失败，回调
     * 消息(带有路由键routingKey)到达交换机，与交换机的所有绑定键进行匹配，匹配不到触发回调
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("returnedMessage ===> replyCode={} ,replyText={} ,exchange={} ,routingKey={}", replyCode, replyText, exchange, routingKey);
        //获取设置的请求头spring_returned_message_correlation 固定的
        String orderSn =
                (String)message.getMessageProperties().getHeader("spring_returned_message_correlation");
        //投递失败 修改状态
        orderMsgService.updateStatus(orderSn,
                replyCode+":"+replyText,
                OrderStatusEnum.OrderMsgStatusEnum.MQ_DELIVERY_FAIL.getCode());


    }
}
