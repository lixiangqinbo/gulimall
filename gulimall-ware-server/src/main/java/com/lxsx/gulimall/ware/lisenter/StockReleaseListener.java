package com.lxsx.gulimall.ware.lisenter;

import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.enume.OrderStatusEnum;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.mq.StockDetailTo;
import com.lxsx.gulimall.mq.StockLockedTo;
import com.lxsx.gulimall.utils.R;
import com.lxsx.gulimall.ware.to.OrderTo;
import com.lxsx.gulimall.ware.constants.RpcStatusCode;
import com.lxsx.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lxsx.gulimall.ware.entity.WareOrderTaskEntity;
import com.lxsx.gulimall.ware.enumm.StockDetailEnum;
import com.lxsx.gulimall.ware.feign.OrderFeignService;
import com.lxsx.gulimall.ware.service.WareOrderTaskDetailService;
import com.lxsx.gulimall.ware.service.WareOrderTaskService;
import com.lxsx.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RabbitListener(queues = MQConstants.STOCK_RELEASE_STOCK_QUEUE)
@Component
@Slf4j
public class StockReleaseListener {

    @Resource
    private WareOrderTaskService wareOrderTaskService;
    @Resource
    private OrderFeignService orderFeignService;
    @Resource
    private WareSkuService wareSkuService;
    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @RabbitHandler
    public void handlerStockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {

        System.out.println("消费到这个消息："+stockLockedTo.toString());
        try{
            //库存任务单详情 有记录说明当前sku库存锁定成功 必须解锁 需要再核对下订单详情，看是都是取消状态
            StockDetailTo detailTo = stockLockedTo.getDetailTo();
            //库存工单id
            Long id = detailTo.getId();
            //拿到库存任务单信息
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(stockLockedTo.getId());
            //订单信息的流水号
            String orderSn = wareOrderTaskEntity.getOrderSn();
            R res = orderFeignService.infoByOrderSn(orderSn);
            if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
                OrderTo orderTo = res.getData(new TypeReference<OrderTo>() {
                });
                //如果是待付款的状态就解锁库存，或者没有该订单
                if (orderTo==null || orderTo.getStatus()== OrderStatusEnum.CREATE_NEW.getCode()) {
                    //反向补偿数据库锁库存数据
                    wareSkuService.updateLockedSku(detailTo);
                    //修改库存工单详情状态为以释放库存
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailService.getById(id);
                    detailTo.setLockStatus(StockDetailEnum.SKU_LOCK_RELEASE.getCode());
                    BeanUtils.copyProperties(detailTo, wareOrderTaskDetailEntity);
                    wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
                }
                //签收消息,并不要求会队列
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }else {
                //调用失败拒绝
                //消息拒绝 让其他服务器处理，让消息回队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            }
        }catch(IOException e){
            //发生错误拒绝
            //消息拒绝 让其他服务器处理 让消息回队列
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        }


    }

    /**
     * todo 再传输对象时 一致报错序列化失败
     * 先改成传orderSn
     * @param orderSn
     * @param message
     * @param channel
     *
     * 订单5分钟不支付就会关闭订单
     */
    @RabbitHandler
    public void  orderReleaseHandler(String orderSn, Message message, Channel channel){
        log.info("监听到点单关闭消息："+orderSn);
        try {
            R res = orderFeignService.infoByOrderSn(orderSn);
            if (res.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
                OrderTo currentOrder = res.getData(new TypeReference<OrderTo>() {
                });
                WareOrderTaskEntity wareOrderTaskEntity =
                        wareOrderTaskService.getByOrderSn(currentOrder.getOrderSn());
                Long wareOrderTaskEntityId = wareOrderTaskEntity.getId();
                List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities =
                        wareOrderTaskDetailService.getByTaskId(wareOrderTaskEntityId);
                for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : wareOrderTaskDetailEntities) {
                    //工作详情单是以锁定才解锁
                    if (wareOrderTaskDetailEntity.getLockStatus()== StockDetailEnum.SKU_LOCK_LOCKED.getCode()) {
                        //更行库存详情单的状态
                        wareOrderTaskDetailEntity.setLockStatus(StockDetailEnum.SKU_LOCK_RELEASE.getCode());
                        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
                        //构建条件
                        StockDetailTo detailTo = new StockDetailTo();
                        detailTo.setSkuId(wareOrderTaskDetailEntity.getSkuId());
                        detailTo.setSkuNum(wareOrderTaskDetailEntity.getSkuNum());
                        detailTo.setWareId(wareOrderTaskDetailEntity.getWareId());
                        //反向补偿库存
                        wareSkuService.updateLockedSku(detailTo);
                    }
                }
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
