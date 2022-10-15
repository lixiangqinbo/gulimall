package com.lxsx.gulimall.order.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.order.component.AliPayTemplates;
import com.lxsx.gulimall.order.component.AlipayTemplate;
import com.lxsx.gulimall.order.constants.OrderConstants;
import com.lxsx.gulimall.order.constants.RpcStatusCode;
import com.lxsx.gulimall.order.entity.OrderEntity;
import com.lxsx.gulimall.order.entity.OrderItemEntity;
import com.lxsx.gulimall.order.entity.OrderMsgEntity;
import com.lxsx.gulimall.order.enume.OrderStatusEnum;
import com.lxsx.gulimall.order.service.OrderItemService;
import com.lxsx.gulimall.order.service.OrderMsgService;
import com.lxsx.gulimall.order.service.OrderService;
import com.lxsx.gulimall.order.web.feign.CartFeignService;
import com.lxsx.gulimall.order.web.feign.MemberAddressFeignService;
import com.lxsx.gulimall.order.web.feign.SkuInfoFeignService;
import com.lxsx.gulimall.order.web.feign.WareSkuFeignService;
import com.lxsx.gulimall.order.web.interceptor.OrderInterceptor;
import com.lxsx.gulimall.order.web.service.OrderWebService;
import com.lxsx.gulimall.order.web.to.SkuPriceTo;
import com.lxsx.gulimall.order.web.vo.*;
import com.lxsx.gulimall.to.LockWareSkuTo;
import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.to.SkuWareTo;
import com.lxsx.gulimall.utils.R;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.json.Json;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderWebServiceImpl implements OrderWebService {

    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private CartFeignService cartFeignService;
    @Resource
    private SkuInfoFeignService skuInfoFeignService;
    @Resource
    private MemberAddressFeignService memberAddressFeignService;
    @Resource
    private WareSkuFeignService wareSkuFeignService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderItemService orderItemService;
    @Resource
    private OrderService orderService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private AlipayTemplate alipayTemplate;
    @Resource
    private AliPayTemplates aliPayTemplates;
    @Resource
    private OrderMsgService orderMsgService;
    @Override
    public OrderConfirmVo queryOrderConfirm() throws ExecutionException, InterruptedException {

        //获取当前线程请求头信息(解决Feign异步调用丢失请求头问题)
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取当前的登录用户信息
        MemberEntityTo memberEntityTo = OrderInterceptor.threadLocal.get();
        //构建返回数据
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        /**
         * 1.查询会员收货地址信息
         * 2.查询购物车选项
         * 3.查购物测sku的最新价格
         */
        //开启异步处理
        CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //远程查询会员收货地址信息
            R res = memberAddressFeignService.memberAddressInfo(memberEntityTo.getId());
            if (res.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                List<MemberReceiveAddressVo> data = res.getData(new TypeReference<List<MemberReceiveAddressVo>>() {
                });
                orderConfirmVo.setMemberAddressVos(data);
            }
        }, executor);
        //开启异步处理
        List<Long> GskuIds = new ArrayList<>();
        CompletableFuture<Void> carItemFuture = CompletableFuture.runAsync(() -> {
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            //查询购物车选项
            R carRes = cartFeignService.queryCartItems(memberEntityTo.getId());

            if (carRes.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                List<CartItemVo> cartItemVos = carRes.getData(new TypeReference<List<CartItemVo>>() {
                });
                //调用一次数据库 手机skuids
                List<Long> skuIds = cartItemVos.stream().map(cartItemVo -> {
                    return cartItemVo.getSkuId();
                }).collect(Collectors.toList());
                GskuIds.addAll(skuIds);
                //查购物测sku的最新价格
                R skuPriceres = skuInfoFeignService.querySkuPrice(skuIds);

                if (skuPriceres.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                    List<SkuPriceTo> skuPriceTos = skuPriceres.getData(new TypeReference<List<SkuPriceTo>>() {
                    });
                    //映射成map 方便以skuid 取出最新价格 :::注意用这个转化Map 需要考虑是都有重复的key 不然会报错，我这里都是id不会重复 放心用
                    Map<Long, BigDecimal> collect = skuPriceTos.stream().collect(Collectors.toMap(SkuPriceTo::getSKUId, SkuPriceTo::getPrice));
                    //更新每个购物项的最新价格
                    List<CartItemVo> cartItems = cartItemVos.stream().map(cartItemVo -> {
                        cartItemVo.setPrice(collect.get(cartItemVo.getSkuId()));
                        return cartItemVo;
                    }).collect(Collectors.toList());
                    //装配数据
                    orderConfirmVo.setItems(cartItems);
                }
            }
        }, executor).thenRunAsync(()->{
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
                R waresku = wareSkuFeignService.getWaresku(GskuIds);
                if (waresku.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
                    List<SkuWareTo> data = waresku.getData(new TypeReference<List<SkuWareTo>>() {
                    });
                    Map<Long, Boolean> collect = data.stream().collect(Collectors.toMap(SkuWareTo::getSkuId, SkuWareTo::isHasStock));
                    orderConfirmVo.setStocks(collect);
                }
        }, executor);

        CompletableFuture.allOf(memberFuture,carItemFuture).get();
        //设置用户积分信息
        orderConfirmVo.setIntegration(memberEntityTo.getIntegration());
        //幂等性问题 制作一个放重token
        String token = UUID.randomUUID().toString().replace("_","");
        //设置token 过期时间
        stringRedisTemplate.opsForValue().set(OrderConstants.RESUBMIT_TOKEN_PRE+memberEntityTo.getId(),token , OrderConstants.TOKEN_TIME_OUT, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        return orderConfirmVo;
    }

    /**
     *
     * 提交订单的信息
     * 保存订单信息
     * @return
     * @param orderSubmitVo
     */
    //TODO 分布式seata 整合找不到服务器
    //@GlobalTransactional //can not get cluster name in registry config 'service.vgroupMapping.gulimall-order-fescar-service-group',
    // please make sure registry config correct
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        /**
         * 1.token 防止重复提交
         * 2.再一次获取最新的sku价格
         * 3.保存订单信息 oms_order
         * 4.保存订单项 oms_order_item
         * 5。验证价格 不做
         * 6。锁库存
         */
        /**
         * 考虑下异步编排 怎么编排 TODO
         */
        SubmitOrderResponseVo submitOrderResponseVo =new SubmitOrderResponseVo();
        //获取当前的登录用户信息
        MemberEntityTo memberEntityTo = OrderInterceptor.threadLocal.get();
        //token 防止重复提交
        String orderToken = orderSubmitVo.getOrderToken();
        String token = stringRedisTemplate.opsForValue().get(OrderConstants.RESUBMIT_TOKEN_PRE + memberEntityTo.getId());
        if (orderToken!=null && orderToken.equals(token)) {
            //令牌的核对和删除必须是原子性 这里我们用redis的脚步解决
            //<T> T execute(RedisScript<T> var1, List<K> var2, Object... var3);
            Long res = stringRedisTemplate.execute(new DefaultRedisScript<Long>(OrderConstants.DELETE_TOKEN_SCRIPT, Long.class),
                    Arrays.asList(OrderConstants.RESUBMIT_TOKEN_PRE + memberEntityTo.getId())
                    , orderToken);
            if (res==1L) { //token验证通过
                //构建订单信息
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(IdWorker.get32UUID());
                orderEntity.setCreateTime(new Date());
                orderEntity.setModifyTime(new Date());
                orderEntity.setAutoConfirmDay(OrderConstants.AUTO_CONFIRM_DAY);
                orderEntity.setMemberId(memberEntityTo.getId());
                //设置地址信息
                Long addrId = orderSubmitVo.getAddrId();
                R memberAddressInfo = memberAddressFeignService.memberAddressInfoByAddrId(addrId);
                if (memberAddressInfo.getCode()!= RpcStatusCode.RPC_SUCCESS_CODE) {
                    //会员地址获取失败
                    submitOrderResponseVo.setCode(OrderConstants.MEMBER_ADRR_FAIL);
                    return submitOrderResponseVo;
                }
                MemberReceiveAddressVo data = memberAddressInfo.getData(new TypeReference<MemberReceiveAddressVo>() {
                });
                if (data==null) {
                    //没有该客户的收获地址 跳转到收货地址添加页面
                    submitOrderResponseVo.setCode(OrderConstants.MEMBER_ADRR_EMPTY);
                    return submitOrderResponseVo;
                }
                orderEntity.setReceiverCity(data.getCity());
                orderEntity.setReceiverDetailAddress(data.getDetailAddress());
                orderEntity.setReceiverProvince(data.getProvince());
                orderEntity.setReceiverPostCode(data.getPostCode());
                orderEntity.setReceiverRegion(data.getRegion());
                orderEntity.setReceiverName(data.getName());
                orderEntity.setReceiverPhone(data.getPhone());
                orderEntity.setTotalAmount(new BigDecimal(1));
                orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
                orderEntity.setDeleteStatus(0);
                BigDecimal growth = new BigDecimal(0);
                //获取所有购物项并且每个购物项都是最新价格
                List<CartItemVo> cartItemVos = getCartItemVoWithNewPrice(memberEntityTo.getId());
                for (CartItemVo cartItemVo : cartItemVos) {
                    BigDecimal multiply = cartItemVo.getPrice().multiply(new BigDecimal(cartItemVo.getCount()));
                    growth = growth.add(multiply);
                }
                //设置积分信息 按照消费1：1
                orderEntity.setGrowth(growth.intValue());
                orderEntity.setIntegration(growth.intValue());
                orderEntity.setPayAmount(growth);

                //锁库存
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setLocks(cartItemVos);
                wareSkuLockVo.setOrderSn(orderEntity.getOrderSn());
                R waresku = wareSkuFeignService.newLockWaresku(wareSkuLockVo);

                if (waresku.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
                    Boolean resLocked = waresku.getData(new TypeReference<Boolean>() {
                    });
                    //库存锁定失败
                    if (!resLocked) {
                        submitOrderResponseVo.setCode(OrderConstants.CHECK_STOKE_FAIL);
                        return submitOrderResponseVo;
                    }

                    //保存订单项 oms_order_item
                    orderItemService.saveItems(cartItemVos,orderEntity.getOrderSn());
                    //保存订单信息
                    orderService.save(orderEntity);
                    submitOrderResponseVo.setOrder(orderEntity);
                    submitOrderResponseVo.setCode(OrderConstants.ORDER_BUILDER_SUCCESS);
                    //当订单创建成功就发送消息到订单死信队列
                    CorrelationData correlationData = new CorrelationData();//设置消息的标识符 这里就用订单号充当
                    correlationData.setId(orderEntity.getOrderSn());
                    rabbitTemplate.convertAndSend(MQConstants.ORDER_EVENT_EXCHANGE,
                            MQConstants.ORDER_CREATE_ORDER,
                            orderEntity,
                            correlationData);
                    //消息存储到消息表中 TODO 未测试
                    OrderMsgEntity orderMsgEntity = new OrderMsgEntity();
                    orderMsgEntity.setOrderSn(orderEntity.getOrderSn());
                    orderMsgEntity.setMsgStatus(OrderStatusEnum.OrderMsgStatusEnum.MQ_NEW.getCode());
                    orderMsgEntity.setMsgJson(JSON.toJSONString(orderEntity));
                    orderMsgService.save(orderMsgEntity);
                    //todo 一旦执行成功就应该扣减库存+删除购物车
                    return submitOrderResponseVo;
                }else {
                    //锁定库存rpc 调用失败
                    submitOrderResponseVo.setCode(OrderConstants.CHECK_STOKE_ERROR);
                    return submitOrderResponseVo;
                }


            }else{ //token验证不通过
                submitOrderResponseVo.setCode(OrderConstants.CHECK_TOKEN_FAIL);
                return submitOrderResponseVo;
            }
        }
        submitOrderResponseVo.setCode(OrderConstants.CHECK_TOKEN_FAIL);

        return submitOrderResponseVo;
    }

    /**
     * 支付接口
     * @param orderSn
     * @return
     */
    @Override
    public String aliPayOrder(String orderSn) throws AlipayApiException {
        OrderEntity byOrderSn = orderService.getByOrderSn(orderSn);
        PayVo payVo = new PayVo();
        //订单号
        payVo.setOut_trade_no(byOrderSn.getOrderSn());
        //支付总额度
        BigDecimal amount = byOrderSn.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(amount.toString());
        //订单的标题
        List<OrderItemEntity> orderItemEntities = orderItemService.queryOrderItems(orderSn);
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

    /**
     * 关闭订单
     * @param orderSn
     * @return
     *
     *   AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
     *                 "app_id",
     *                 "your private_key",
     *                 "json",
     *                 "GBK",
     *                 "alipay_public_key",
     *                 "RSA2");
     */
    @Override
    public boolean closeOrder(String orderSn) throws AlipayApiException {
        boolean response = aliPayTemplates.closeOrder(orderSn);
        if(response){
            System.out.println("调用成功");
            //取消订单
            OrderEntity orderEntity = orderService.getByOrderSn(orderSn);
            if (orderEntity.getStatus()== OrderStatusEnum.CREATE_NEW.getCode()) {
                orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
                orderService.updateById(orderEntity);
            }
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }

    }

    @Override
    public String queryOrder(String orderSn) throws AlipayApiException {
        AlipayTradeQueryResponse alipayTradeQueryResponse = aliPayTemplates.queryOrder(orderSn);
        String responseJson = JSON.toJSONString(alipayTradeQueryResponse);
        return responseJson;
    }

    /**
     * 获取购物车的购物项并且 返回购物所有购物项的最新价格
     * @param memberId
     * @return
     */
    public List<CartItemVo> getCartItemVoWithNewPrice(Long memberId) {
        //查询购物车选项
        R carRes = cartFeignService.queryCartItems(memberId);
        List<Long> GskuIds = new ArrayList<>();
        if (carRes.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
            List<CartItemVo> cartItemVos = carRes.getData(new TypeReference<List<CartItemVo>>() {
            });
            //调用一次数据库 手机skuids
            List<Long> skuIds = cartItemVos.stream().map(cartItemVo -> {
                return cartItemVo.getSkuId();
            }).collect(Collectors.toList());
            GskuIds.addAll(skuIds);
            //查购物测sku的最新价格
            R skuPriceres = skuInfoFeignService.querySkuPrice(skuIds);

            if (skuPriceres.getCode() == RpcStatusCode.RPC_SUCCESS_CODE) {
                List<SkuPriceTo> skuPriceTos = skuPriceres.getData(new TypeReference<List<SkuPriceTo>>() {
                });
                //映射成map 方便以skuid 取出最新价格 :::注意用这个转化Map 需要考虑是都有重复的key 不然会报错，我这里都是id不会重复 放心用
                Map<Long, BigDecimal> collect = skuPriceTos.stream().collect(Collectors.toMap(SkuPriceTo::getSKUId, SkuPriceTo::getPrice));
                //更新每个购物项的最新价格
                List<CartItemVo> cartItems = cartItemVos.stream().map(cartItemVo -> {
                    cartItemVo.setPrice(collect.get(cartItemVo.getSkuId()));
                    return cartItemVo;
                }).collect(Collectors.toList());
                //装配数据
                return cartItems;
            }
            return cartItemVos;
        }else {
            return null;
        }

    }



    /**
     *
     * 提交订单的信息
     * 保存订单信息
     * @return
     * @param orderSubmitVo
     */
    //TODO 分布式seata 整合找不到服务器
    //@GlobalTransactional //can not get cluster name in registry config 'service.vgroupMapping.gulimall-order-fescar-service-group',
    // please make sure registry config correct
    @Transactional
    public SubmitOrderResponseVo submitOrder1(OrderSubmitVo orderSubmitVo) throws ExecutionException, InterruptedException {
        /**
         * 1.token 防止重复提交
         * 2.再一次获取最新的sku价格
         * 3.保存订单信息 oms_order
         * 4.保存订单项 oms_order_item
         * 5。验证价格 不做
         * 6。锁库存
         */
        /**
         * 考虑下异步编排 怎么编排 TODO
         */
        SubmitOrderResponseVo submitOrderResponseVo =new SubmitOrderResponseVo();
        //获取当前的登录用户信息
        MemberEntityTo memberEntityTo = OrderInterceptor.threadLocal.get();
        //token 防止重复提交
        String orderToken = orderSubmitVo.getOrderToken();
        String token = stringRedisTemplate.opsForValue().get(OrderConstants.RESUBMIT_TOKEN_PRE + memberEntityTo.getId());
        if (orderToken!=null && orderToken.equals(token)) {
            //令牌的核对和删除必须是原子性 这里我们用redis的脚步解决
            //<T> T execute(RedisScript<T> var1, List<K> var2, Object... var3);
            Long res = stringRedisTemplate.execute(new DefaultRedisScript<Long>(OrderConstants.DELETE_TOKEN_SCRIPT, Long.class),
                    Arrays.asList(OrderConstants.RESUBMIT_TOKEN_PRE + memberEntityTo.getId())
                    , orderToken);
            if (res==1L) { //token验证通过
                //构建订单信息
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(IdWorker.get32UUID());
                orderEntity.setCreateTime(new Date());
                orderEntity.setModifyTime(new Date());
                orderEntity.setAutoConfirmDay(OrderConstants.AUTO_CONFIRM_DAY);
                orderEntity.setMemberId(memberEntityTo.getId());

                CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
                    //设置地址信息
                    Long addrId = orderSubmitVo.getAddrId();
                    R memberAddressInfo = memberAddressFeignService.memberAddressInfoByAddrId(addrId);
                    if (memberAddressInfo.getCode() != RpcStatusCode.RPC_SUCCESS_CODE) {
                        //会员地址获取失败
                        submitOrderResponseVo.setCode(OrderConstants.MEMBER_ADRR_FAIL);
                        return submitOrderResponseVo;
                    }
                    MemberReceiveAddressVo data = memberAddressInfo.getData(new TypeReference<MemberReceiveAddressVo>() {
                    });
                    if (data==null) {
                        //没有该客户的收获地址 跳转到收货地址添加页面
                        submitOrderResponseVo.setCode(OrderConstants.MEMBER_ADRR_EMPTY);
                        return submitOrderResponseVo;
                    }
                    orderEntity.setReceiverCity(data.getCity());
                    orderEntity.setReceiverDetailAddress(data.getDetailAddress());
                    orderEntity.setReceiverProvince(data.getProvince());
                    orderEntity.setReceiverPostCode(data.getPostCode());
                    orderEntity.setReceiverRegion(data.getRegion());
                    orderEntity.setReceiverName(data.getName());
                    orderEntity.setReceiverPhone(data.getPhone());
                    orderEntity.setTotalAmount(new BigDecimal(1));
                    orderEntity.setConfirmStatus(OrderStatusEnum.CREATE_NEW.getCode());
                    orderEntity.setDeleteStatus(0);

                    return submitOrderResponseVo;
                }, executor);

                CompletableFuture<List<CartItemVo>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    BigDecimal growth = new BigDecimal(0);
                    //获取所有购物项并且每个购物项都是最新价格
                    List<CartItemVo> cartItemVos = getCartItemVoWithNewPrice(memberEntityTo.getId());
                    for (CartItemVo cartItemVo : cartItemVos) {
                        BigDecimal multiply = cartItemVo.getPrice().multiply(new BigDecimal(cartItemVo.getCount()));
                        growth = growth.add(multiply);
                    }
                    //设置积分信息 按照消费1：1
                    orderEntity.setGrowth(growth.intValue());
                    orderEntity.setIntegration(growth.intValue());
                    orderEntity.setPayAmount(growth);
                    return cartItemVos;
                }, executor);
                List<CartItemVo> cartItemVos1 = listCompletableFuture.get();

                CompletableFuture<R> rCompletableFuture = listCompletableFuture.thenApplyAsync((cartItemVos) -> {
                    /**
                     * 远程查询库存信息
                     */
                    R waresku = getLockWareSkuTo(cartItemVos);
                    return waresku;
                }, executor);
                R waresku = rCompletableFuture.get();

                if (waresku.getCode()== RpcStatusCode.RPC_SUCCESS_CODE) {
                    List<LockWareSkuTo> lockWareSkuTos1 = waresku.getData(new TypeReference<List<LockWareSkuTo>>() {
                    });
                    //收集库存不足的购物项
                    Map<Long,String> failCartItemVos = new HashMap<>();
                    for (LockWareSkuTo lockWareSkuTo : lockWareSkuTos1) {
                        if (!lockWareSkuTo.getIsLock()){
                            failCartItemVos.put(lockWareSkuTo.getSkuId(),OrderConstants.FAIL_SKU_MSG);
                        }
                    }
                    //记录 那些sku锁定库存失败
                    submitOrderResponseVo.setFailCartItems(failCartItemVos);
                    //库存锁定失败
                    if (failCartItemVos.size()>0) {
                        submitOrderResponseVo.setCode(OrderConstants.CHECK_STOKE_FAIL);
                        return submitOrderResponseVo;
                    }
                    
                    CompletableFuture.runAsync(()->{
                        //保存订单项 oms_order_item
                        orderItemService.saveItems(cartItemVos1,orderEntity.getOrderSn());
                    }, executor);

                    CompletableFuture.runAsync(()->{
                        //保存订单信息
                        orderService.save(orderEntity);
                    }, executor);


                    submitOrderResponseVo.setOrder(orderEntity);
                    submitOrderResponseVo.setCode(OrderConstants.ORDER_BUILDER_SUCCESS);
                    //todo 一旦执行成功就应该扣减库存+删除购物车
                    return submitOrderResponseVo;
                }else {
                    //锁定库存rpc 调用失败
                    submitOrderResponseVo.setCode(OrderConstants.CHECK_STOKE_ERROR);
                    return submitOrderResponseVo;
                }


            }else{ //token验证不通过
                submitOrderResponseVo.setCode(OrderConstants.CHECK_TOKEN_FAIL);
                return submitOrderResponseVo;
            }
        }
        submitOrderResponseVo.setCode(OrderConstants.CHECK_TOKEN_FAIL);

        return submitOrderResponseVo;
    }

    /**
     * 远程查看库存信息
     * @param cartItemVos
     * @return
     */
    private R getLockWareSkuTo(List<CartItemVo> cartItemVos) {
        //锁库存
        List<LockWareSkuTo> lockWareSkuTos = cartItemVos.stream().map(cartItemVo -> {
            LockWareSkuTo lockWareSkuTo = new LockWareSkuTo();
            lockWareSkuTo.setSkuId(cartItemVo.getSkuId());
            lockWareSkuTo.setNum(cartItemVo.getCount());
            //这里指定仓库库存 有时间考虑下 多库存解决方案
            lockWareSkuTo.setWareId(1L);
            return lockWareSkuTo;
        }).collect(Collectors.toList());
        return wareSkuFeignService.lockWaresku(lockWareSkuTos);
    }

}
