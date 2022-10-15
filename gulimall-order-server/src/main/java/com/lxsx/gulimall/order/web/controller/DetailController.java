package com.lxsx.gulimall.order.web.controller;

import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.UUID;

@Controller
public class DetailController {

    @Resource
    private RabbitTemplate rabbitTemplate;


    @GetMapping("/detail")
    public String detail(){

        return "detail";
    }

    @ResponseBody
    @GetMapping("/testOrder")
    public String testOrder(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(MQConstants.ORDER_EVENT_EXCHANGE,MQConstants.ORDER_RELEASE_ORDER,orderEntity);
        return "ok";
    }
}
