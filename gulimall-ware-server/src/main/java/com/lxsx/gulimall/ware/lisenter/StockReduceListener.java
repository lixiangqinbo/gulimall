package com.lxsx.gulimall.ware.lisenter;

import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.enume.OrderStatusEnum;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.ware.constants.RpcStatusCode;
import com.lxsx.gulimall.ware.feign.OrderFeignService;
import com.lxsx.gulimall.ware.service.WareOrderTaskService;
import com.lxsx.gulimall.ware.to.OrderTo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@RabbitListener(queues = MQConstants.STOCK_REDUCE_STOCK_QUEUE)
@Slf4j
public class StockReduceListener {
    @Resource
    private OrderFeignService orderFeignService;
    @Resource
    private WareOrderTaskService wareOrderTaskService;

    @RabbitListener
    public void stockReduceHandler(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("消费到点单支付成功消息：{}"+orderTo.toString());
        String orderSn = orderTo.getOrderSn();
        R res = orderFeignService.infoByOrderSn(orderSn);
        if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
            OrderTo currentOrder = res.getData(new TypeReference<OrderTo>() {
            });
            //再次确定是否已付款状态
            if (currentOrder.getStatus()== OrderStatusEnum.PAYED.getCode()) {
                //更改库存
                wareOrderTaskService.updateTaskAndWare(currentOrder.getOrderSn());
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }else{
            //远程失败调用 决绝签收 并归队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
