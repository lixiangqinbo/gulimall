package com.lxsx.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.component.AlipayTemplate;
import com.lxsx.gulimall.order.constants.RpcStatusCode;
import com.lxsx.gulimall.order.entity.OrderItemEntity;
import com.lxsx.gulimall.order.entity.PaymentInfoEntity;
import com.lxsx.gulimall.order.enume.OrderStatusEnum;
import com.lxsx.gulimall.order.service.OrderItemService;
import com.lxsx.gulimall.order.service.PaymentInfoService;
import com.lxsx.gulimall.order.web.feign.WareSkuFeignService;
import com.lxsx.gulimall.order.web.interceptor.OrderInterceptor;
import com.lxsx.gulimall.order.web.vo.PayAsyncVo;
import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.utils.R;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxsx.gulimall.utils.PageUtils;
import com.lxsx.gulimall.utils.Query;

import com.lxsx.gulimall.order.dao.OrderDao;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.lxsx.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private OrderItemService orderItemService;
    @Resource
    private PaymentInfoService paymentInfoService;
    @Resource
    private OrderService orderService;
    @Resource
    private AlipayTemplate alipayTemplate;
    @Resource
    private WareSkuFeignService wareSkuFeignService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 支付的lock
     */
    private final String ALIPAY_LOCAK= "alipay_lock:";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderEntity getByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.baseMapper.selectOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
        return orderEntity;
    }

    /**
     * 查询订单数据和 订单详情
     * @param params
     * @return
     */
    @Override
    public PageUtils queryDetailPage(Map<String, Object> params) {
        IPage<OrderEntity> page = new Query<OrderEntity>().getPage(params);

        MemberEntityTo memberEntityTo = OrderInterceptor.threadLocal.get();
        Long memberEntityToId = memberEntityTo.getId();
        /**
         * 优化： 这个可以先脸表查询后 然后再组装数据如果数据太多的话
         */
        //该用户的所有订单
        List<OrderEntity> orderEntities = this.baseMapper.selectList(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getMemberId, memberEntityToId));
        List<OrderEntity> entities = orderEntities.stream().map(orderEntity -> {
            String orderSn = orderEntity.getOrderSn();
            List<OrderItemEntity> orderItemEntities = orderItemService.queryOrderItems(orderSn);
            orderEntity.setOrderItemEntityList(orderItemEntities);
            return orderEntity;
        }).collect(Collectors.toList());

        page.setRecords(entities);
        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    /**
     * 支付宝处理的结果
     * @param payAsyncVo
     * @return
     */
    @Override
    @Transactional
    public Boolean orderPayedHanlder(PayAsyncVo payAsyncVo) {

        String trade_status = payAsyncVo.getTrade_status();
        String orderSn = payAsyncVo.getOut_trade_no();
        String total_amount = payAsyncVo.getTotal_amount();
        String app_id = payAsyncVo.getApp_id();
        String seller_id = payAsyncVo.getSeller_id();
        OrderEntity orderEntity = orderService.getByOrderSn(orderSn);
        //判断是否为重复通知
        if (orderEntity.getStatus()== OrderStatusEnum.PAYED.getCode()) {
            return true;
        }
        //保存交易流水
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setCallbackTime(payAsyncVo.getNotify_time());
        paymentInfoEntity.setConfirmTime(payAsyncVo.getNotify_time());
        paymentInfoEntity.setCreateTime(payAsyncVo.getNotify_time());
        paymentInfoEntity.setOrderSn(orderSn);
        paymentInfoEntity.setPaymentStatus(trade_status);
        //保存支付流水
        paymentInfoService.save(paymentInfoEntity);
        /**
         * 业务二次校验：
         * 1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
         * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
         * 3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
         * 4、验证app_id是否为该商户本身。
         */
        if (orderEntity==null
                ||orderEntity.getPayAmount().compareTo(new BigDecimal(total_amount))!=0
                ||!alipayTemplate.getPid().equals(seller_id)
                ||!alipayTemplate.getApp_id().equals(app_id)) {
            return false;
        }
        //加锁
        RLock lock = redissonClient.getLock(ALIPAY_LOCAK);
        lock.lock(3, TimeUnit.SECONDS);
        //支付状态为成功
        if (OrderStatusEnum.OrderPayedStatusEnum.TRADE_SUCCESS.getRes().equals(trade_status)
        ||OrderStatusEnum.OrderPayedStatusEnum.TRADE_FINISHED.equals(trade_status)) {
            try{
                //修改订单状态
                orderEntity.setStatus(OrderStatusEnum.PAYED.getCode());
                orderService.updateById(orderEntity);
                //订单保存成功后就应该读工单解锁库存
                R taskInfo = wareSkuFeignService.updateTaskAndWare(orderSn);
                if (taskInfo.getCode()!= RpcStatusCode.RPC_SUCCESS_CODE) {
                    //如果远程访问失败，就给MQ发消息
                    rabbitTemplate.convertAndSend(MQConstants.STOCK_EVENT_EXCHANGE,
                            MQConstants.STOCK_REDUCE,
                            orderEntity);
                }
            }catch (Exception e){
                //如果远程访问失败，就给MQ发消息
                rabbitTemplate.convertAndSend(MQConstants.STOCK_EVENT_EXCHANGE,
                        MQConstants.STOCK_REDUCE,
                        orderEntity);
            }finally {
                //解锁
                lock.unlock();
            }

            return true;

        }else {

            return false;
        }
    }

}