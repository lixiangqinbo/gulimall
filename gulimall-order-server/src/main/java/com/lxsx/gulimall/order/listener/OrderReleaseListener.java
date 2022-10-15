package com.lxsx.gulimall.order.listener;

import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.lxsx.gulimall.order.enume.OrderStatusEnum;
import com.lxsx.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@RabbitListener(queues = MQConstants.ORDER_RELEASE_ORDER_QUEUE)
@Slf4j
@Component
public class OrderReleaseListener {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderService orderService;


    @RabbitHandler
    public void orderRelease(OrderEntity orderEntity, Message message, Channel channel){
        log.info("监听到点单关闭消息："+orderEntity);

        try {
            Long orderId = orderEntity.getId();
            OrderEntity currentOrder = orderService.getById(orderId);
            //判断是单是否还是未付款
            if (currentOrder.getStatus()== OrderStatusEnum.CREATE_NEW.getCode()) {
                //未付款状态就修改为取消状态
                currentOrder.setStatus(OrderStatusEnum.CANCLED.getCode());
                orderService.updateById(currentOrder);
                //取消后给库存队列发消息 订单已经取消
                rabbitTemplate.convertAndSend(MQConstants.ORDER_EVENT_EXCHANGE,MQConstants.STOCK_RELEASE_OTHER, currentOrder.getOrderSn());
            }
            //签收
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            try {
                //拒绝签收 重回队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
}
