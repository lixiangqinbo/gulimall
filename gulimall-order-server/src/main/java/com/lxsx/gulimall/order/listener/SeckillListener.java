package com.lxsx.gulimall.order.listener;

import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.lxsx.gulimall.order.entity.OrderItemEntity;
import com.lxsx.gulimall.order.service.OrderItemService;
import com.lxsx.gulimall.order.service.OrderService;
import com.lxsx.gulimall.order.web.to.SpuInfoTo;
import com.lxsx.gulimall.to.OrderEntityTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RabbitListener(queues = MQConstants.SECKILL_ORDER_QUEUE)
@Slf4j
@Component
public class SeckillListener {
    @Resource
    private OrderService orderService;
    @Resource
    private OrderItemService orderItemService;
    @RabbitHandler
    public void seckillOrderHandler(OrderEntityTo orderEntityTo, Message message, Channel channel){
        log.info("收到秒杀单消息：{}"+orderEntityTo.toString());
        // 构建订单 并保存
        OrderEntity orderEntity = new OrderEntity();
        String orderSn = orderEntityTo.getOrderSn();
        BeanUtils.copyProperties(orderEntityTo,orderEntity);
        orderService.save(orderEntity);
        //构建订单项并保存
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        Long skuId = orderEntityTo.getSkuId();
        SpuInfoTo spuInfoTo = orderItemService.getSpuinfoBySkuId(skuId);
        orderItemEntity.setOrderId(orderEntity.getId());
        orderItemEntity.setOrderSn(orderSn);
        orderItemEntity.setSpuBrand(spuInfoTo.getBrandName());
        orderItemEntity.setCategoryId(spuInfoTo.getCatalogId());
        orderItemEntity.setSpuName(spuInfoTo.getSpuName());
        orderItemEntity.setSpuId(spuInfoTo.getSpuId());
        orderItemEntity.setSkuId(skuId);
        orderEntity.setPayAmount(orderEntityTo.getPayAmount());
        orderItemService.save(orderItemEntity);

    }
}
