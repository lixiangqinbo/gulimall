package com.lxsx.gulimall.controller;

import com.lxsx.gulimall.utils.R;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class RabbitController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/ok/{messages}")
    public R testConfirm(@PathVariable("messages") String messages){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        CorrelationData correlationData = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(
                "test_exchange",
                "test.queue.test",
                messages,
                correlationData);
        return R.ok();
    }

    /**
     * Broker消息接收失败! correlationData=e102c0dce35e4c96b425c02a1d5f19af ,ack=false, cause=channel error;
     * protocol method: #method<channel.close>
     * (reply-code=404, reply-text=NOT_FOUND - no exchange 'aaaa' in vhost '/', class-id=60, method-id=40)
     * @param messages
     * @return
     */
    @RequestMapping("/errorExchange/{messages}")
    public R errorExchange(@PathVariable("messages") String messages){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        CorrelationData correlationData = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(
                "aaaa",
                "test_queue",
                messages,
                correlationData);
        return R.ok();
    }

    /**
     * Broker路由QUEUE失败:returnedMessage ===> replyCode=312 ,replyText=NO_ROUTE ,exchange=test_exchange ,routingKey=aaaa,
     * message=(Body:'"ashgaj"' MessageProperties
     *[headers={spring_returned_message_correlation=6fcf1d23d99c46bdb177f807cc0a27bc, __TypeId__=java.lang.String},
     * contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
     *
     * Broker消息接收成功correlationData=b3aae8c7c17343169a0eabb4492e046c ,ack=true, cause=null
     * @param messages
     * @return
     */
    @RequestMapping("/errorRouteKey/{messages}")
    public R errorRouteKey(@PathVariable("messages") String messages){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        CorrelationData correlationData = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(
                "test_exchange",
                "aaaa",
                messages,
                correlationData);
        return R.ok();
    }
}
