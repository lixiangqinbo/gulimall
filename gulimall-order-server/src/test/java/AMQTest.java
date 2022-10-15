import com.lxsx.gulimall.order.OrderServerApp;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.rabbitmq.client.AMQP;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest(classes = OrderServerApp.class)
public class AMQTest {

    @Resource
    private AmqpAdmin amqpAdmin;
    @Resource
    private RabbitTemplate rabbitTemplate;


    @Test
    public void AmqpTest(){
        //创建交换机
        DirectExchange directExchange = new DirectExchange("test.direct.exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        //创建队列
        Queue queue = new Queue("test.queues");
        amqpAdmin.declareQueue(queue);
        //创建邦迪器
        Binding binding = new Binding("test.queues",
                Binding.DestinationType.QUEUE,
                "test.direct.exchange",
                "test.queues",
                null);
        amqpAdmin.declareBinding(binding);
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setReceiveTime(new Date());
        orderEntity.setBillContent("dada");
        rabbitTemplate.convertAndSend("test.queues",orderEntity);

    }
}
