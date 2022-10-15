package com.lxsx.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lxsx.gulimall.constants.RpcStatusCode;
import com.lxsx.gulimall.enume.OrderStatusEnum;
import com.lxsx.gulimall.exception.NotFindInfoException;
import com.lxsx.gulimall.exception.RpcException;
import com.lxsx.gulimall.exception.SecKillRuleException;
import com.lxsx.gulimall.mq.MQConstants;
import com.lxsx.gulimall.seckill.feign.ProductFeignService;
import com.lxsx.gulimall.seckill.feign.SecKillSessionFeignService;
import com.lxsx.gulimall.seckill.interceptor.SecKillIterceptor;
import com.lxsx.gulimall.seckill.service.SecKillSkuService;
import com.lxsx.gulimall.seckill.vo.SeckillSessionEntityVo;
import com.lxsx.gulimall.seckill.vo.SeckillSkuRelationEntityVo;
import com.lxsx.gulimall.seckill.vo.SkuInfoEntityVo;
import com.lxsx.gulimall.to.MemberEntityTo;
import com.lxsx.gulimall.to.OrderEntityTo;
import com.lxsx.gulimall.utils.R;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SecKillSkuServiceImpl implements SecKillSkuService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SecKillSessionFeignService secKillSessionFeignService;
    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 秒杀场次的key前缀
     */
    private final String SESSION_CACHE_PREFIX="seckill:sessions";
    /**
     * sku的key
     */
    private final String SECKILL_CACHE_PREFIX="seckill:skus";
    /**
     * 秒杀商品信号量
     */
    private final String SKU_STOCK_SEMAPHORES="seckill:stock:";

    /**
     * 定时上架最近3天的秒杀产品
     */
    @Override
    public void updateSeckillSkuFuture3days() throws Exception {

        //最近三天的秒杀活动信息
        List<SeckillSessionEntityVo> seckillSessionEntityVos = getSeckillSessionEntityVos();
        //最近三天的秒杀活动场次与参与sku信息
        List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos = getSeckillSkuRelationEntityVos(seckillSessionEntityVos);
        //最近三天的秒杀活动sku信息
        List<SkuInfoEntityVo> skuInfos = getSkuInfos(seckillSkuRelationEntityVos);

        //缓存活动信息List结构保存 key:value 开始时间_结束时间:skuIds
        saveSessionInfo(seckillSessionEntityVos,seckillSkuRelationEntityVos);
        //缓存活动关联的商品信息Hash结构保存 Hash<seckill:skus,HASH<skuId,sku>>
        saveSessionSkuInfo(seckillSessionEntityVos,seckillSkuRelationEntityVos,skuInfos);

    }

    /**
     * 缓存活动关联的商品信息Hash结构保存
     * Hash<键,HASH<键,值>>
     * Hash<seckill:skus,HASH<skuId,sku>>
     * @param seckillSessionEntityVos
     * @param seckillSkuRelationEntityVos
     * @param skuInfos
     */
    @Override
    public void saveSessionSkuInfo(List<SeckillSessionEntityVo> seckillSessionEntityVos,
                                    List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos,
                                    List<SkuInfoEntityVo> skuInfos) {
        //将sku 信息封装为map skuIds:sku
        Map<Long, SkuInfoEntityVo> collect =
                skuInfos.stream().collect(Collectors.toMap(SkuInfoEntityVo::getSkuId, skuInfoEntityVo -> skuInfoEntityVo));
        //将场次 信息封装为map sessionIds:session
        Map<Long, SeckillSessionEntityVo> collect1 =
                seckillSessionEntityVos.stream().collect(Collectors.toMap(SeckillSessionEntityVo::getId, item -> item));
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
        for (SeckillSkuRelationEntityVo seckillSkuRelationEntityVo : seckillSkuRelationEntityVos) {
            //商品的随机码，避免恶意刷 sexkill?skuId=1&key=**** 随机码只有开始秒杀才会暴露，防止有人拿到skuId恶意攻击
            String token = UUID.randomUUID().toString().replace("-", "");
            //保存skuId
            Long skuId = seckillSkuRelationEntityVo.getSkuId();
            //保存sessionId
            Long promotionSessionId = seckillSkuRelationEntityVo.getPromotionSessionId();
            //活动场次+skuId 拼 key 单skuId 为key：不能保证唯一，同一个sku科能出现再不同场次的活动中
            String key = promotionSessionId + "_" + skuId.toString();
            //判断是否已经存在
            Boolean aBoolean = boundHashOperations.hasKey(key);
            if (!aBoolean) {
                //封装sku信息
                SkuInfoEntityVo skuInfoEntityVo = collect.get(skuId);
                seckillSkuRelationEntityVo.setSkuInfoEntityVo(skuInfoEntityVo);
                //封装活动场次时间信息
                SeckillSessionEntityVo seckillSessionEntityVo = collect1.get(promotionSessionId);
                long startTime = seckillSessionEntityVo.getStartTime().getTime();
                long endTime = seckillSessionEntityVo.getEndTime().getTime();
                seckillSkuRelationEntityVo.setStartTime(startTime);
                seckillSkuRelationEntityVo.setEndTime(endTime);
                //封装一个随机码
                seckillSkuRelationEntityVo.setRandomCode(token);
                //序列化为json数据
                String jsonString = JSON.toJSONString(seckillSkuRelationEntityVo);
                //缓存sku数据
                boundHashOperations.expire((endTime-startTime),TimeUnit.MILLISECONDS);
                boundHashOperations.put(key,jsonString);
                //高并发操作，引入分布式信号量；限流
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORES + token);
                //设置 允许秒杀的sku的秒杀库存量
                semaphore.trySetPermits(seckillSkuRelationEntityVo.getSeckillCount().intValue());
            }
        }

    }

    /**
     * 缓存活动信息 key:value 开始时间_结束时间:skuIds
     * @param seckillSessionEntityVos
     * @param seckillSkuRelationEntityVos
     */
    @Override
    public void saveSessionInfo(List<SeckillSessionEntityVo> seckillSessionEntityVos, List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos) {
        seckillSessionEntityVos.stream().forEach(seckillSessionEntityVo -> {
            //保存redis
            long startTime = seckillSessionEntityVo.getStartTime().getTime();
            long endTime = seckillSessionEntityVo.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX+startTime+"_"+endTime;
            //判断是redis中是否已经存在这个key
            Boolean aBoolean = stringRedisTemplate.hasKey(key);
            if (!aBoolean) {
                List<String> sessionIdAndskuIds = seckillSkuRelationEntityVos.stream().filter(seckillSkuRelationEntityVo -> {
                    if (seckillSessionEntityVo.getId() == seckillSkuRelationEntityVo.getPromotionSessionId()) {
                        return true;
                    }
                    return false;
                }).map(seckillSkuRelationEntityVo ->seckillSessionEntityVo.getId().toString()+"_"+seckillSkuRelationEntityVo.getSkuId().toString()).collect(Collectors.toList());
                //每个场次设置过期时间
                stringRedisTemplate.expire(key,(endTime-startTime), TimeUnit.MILLISECONDS);
                stringRedisTemplate.opsForList().leftPushAll(key, sessionIdAndskuIds);
            }
        });

    }

    /**
     * 查询有关sku的详细信息
     * @param seckillSkuRelationEntityVos
     * @return
     * @throws RpcException
     * @throws NotFindInfoException
     */
    @Override
    public List<SkuInfoEntityVo> getSkuInfos(List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos) throws RpcException, NotFindInfoException {
        //查询有关sku的详细信息 减轻数据库压力 这里再查询前给list去重复
        List<Long> skuIds = seckillSkuRelationEntityVos.stream().map(SeckillSkuRelationEntityVo::getSkuId).distinct().collect(Collectors.toList());
        R proRes = productFeignService.querySkuInfoBySkuIds(skuIds);
        if (proRes.getCode()!= RpcStatusCode.RPC_SUCCESS_CODE) {
            throw new RpcException("远程查询近三天场次信息获取所有的参与秒杀的sku信息异常！");
        }
        List<SkuInfoEntityVo> skuInfoEntityVos = proRes.getData(new TypeReference<List<SkuInfoEntityVo>>() {
        });
        if (skuInfoEntityVos==null||skuInfoEntityVos.size()==0) {
            throw new NotFindInfoException("远程查询近三天场次信息获取所有的参与秒杀的sku信息:无！");
        }
        return skuInfoEntityVos;
    }

    /**
     * 根据近三天场次信息获取所有的参与秒杀的sku信息关系信息
     * @param seckillSessionEntityVos
     * @return
     * @throws RpcException
     * @throws NotFindInfoException
     */
    @Override
    public List<SeckillSkuRelationEntityVo> getSeckillSkuRelationEntityVos(List<SeckillSessionEntityVo> seckillSessionEntityVos) throws RpcException, NotFindInfoException {
        //根据近三天场次信息获取所有的参与秒杀的sku信息关系信息
        List<Long> killSessinIds =
                seckillSessionEntityVos.stream().map(SeckillSessionEntityVo::getId).collect(Collectors.toList());
        R r = secKillSessionFeignService.querySecKillSessionRelation(killSessinIds);
        if (r.getCode()!= RpcStatusCode.RPC_SUCCESS_CODE) {
            throw new RpcException("远程查询近三天场次信息获取所有的参与秒杀的sku信息关系信息异常！");
        }
        List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos
                = r.getData(new TypeReference<List<SeckillSkuRelationEntityVo>>() {});
        if (seckillSkuRelationEntityVos==null||seckillSessionEntityVos.size()==0) {
            throw new NotFindInfoException("查询近三天场次信息获取所有的参与秒杀的sku信息关系信息:无！");
        }
        return seckillSkuRelationEntityVos;
    }

    /**
     * 查询近三天的活动场次
     * @return
     * @throws RpcException
     * @throws NotFindInfoException
     */
    @Override
    public List<SeckillSessionEntityVo> getSeckillSessionEntityVos() throws RpcException, NotFindInfoException {
        //查询近三天的活动场次
        R res = secKillSessionFeignService.querySecKillSession();
        //远程调用成功
        if (res.getCode() != RpcStatusCode.RPC_SUCCESS_CODE) {
            throw new RpcException("远程查询近三天的秒杀活动场次异常！");
        }
        //获取近三天场次信息
        List<SeckillSessionEntityVo> seckillSessionEntityVos =
                res.getData(new TypeReference<List<SeckillSessionEntityVo>>() {});
        if (seckillSessionEntityVos==null||seckillSessionEntityVos.size()==0) {
            throw new NotFindInfoException("查询近三天的秒杀活动场次：无！");
        }
        return seckillSessionEntityVos;
    }

    /**
     * 查询当前场次参与秒杀的商品
     * @return
     */
    @Override
    public List<SeckillSkuRelationEntityVo> queryCurrentSeckillSkus() throws NotFindInfoException {
        //当前时间 1970前开始计算的
        long currentTime = new Date().getTime();
        Set<String> keys = stringRedisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        if (keys==null||keys.size()==0) {
            throw new NotFindInfoException("redis无相关缓存信息");
        }
        List<SeckillSkuRelationEntityVo> seckillSkuRelationEntityVos =null;
        for (String key : keys) {
            String seesionTime = key.replace(SESSION_CACHE_PREFIX, "");
            String[] time = seesionTime.split("_");
            Long startTime = Long.valueOf(time[0]);
            Long endTime = Long.valueOf(time[1]);

            if (currentTime >= startTime && currentTime <= endTime){
                //当前 key 就是本场次的 获取所有值
                List<String> sessionId_skuIds = stringRedisTemplate.opsForList().range(key, 0, -1);

                seckillSkuRelationEntityVos = sessionId_skuIds.stream().map(sessionId_skuId -> {
                    BoundHashOperations<String, String, String> boundHashOperations =
                            stringRedisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
                    String secKillSkuInfos = boundHashOperations.get(sessionId_skuId);
                    //包含秒杀数量 价格 开始结束时间都在里面 sku详细信息
                    SeckillSkuRelationEntityVo seckillSkuRelationEntityVo =
                            JSON.parseObject(secKillSkuInfos, SeckillSkuRelationEntityVo.class);
                    return seckillSkuRelationEntityVo;
                }).collect(Collectors.toList());
                break;
            }
        }
        if (seckillSkuRelationEntityVos!=null&&seckillSkuRelationEntityVos.size()!=0) {
            return seckillSkuRelationEntityVos;
        }else {
            throw new NotFindInfoException("该场次没有秒杀sku");
        }
    }

    /**
     * 更具skuId查询这个商品的秒杀信息
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRelationEntityVo querySeckillSkuBySkuId(Long skuId) {
        BoundHashOperations<String, String, String> boundHashOperations = stringRedisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
        Set<String> keys = boundHashOperations.keys();
        SeckillSkuRelationEntityVo seckillSkuRelationEntityVo = null;
        //正则：随意数字_+skuid
        String regx = "\\d_"+skuId;
        if (keys!=null&&keys.size()>0) {
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String s = boundHashOperations.get(key);
                    seckillSkuRelationEntityVo =
                            JSON.parseObject(s, SeckillSkuRelationEntityVo.class);
                    //随机码处理
                    long currentTime = new Date().getTime();
                    if (currentTime >= seckillSkuRelationEntityVo.getStartTime() &&
                            currentTime <= seckillSkuRelationEntityVo.getEndTime()){

                    }else{
                        //不在秒杀时间不展示随机码
                        seckillSkuRelationEntityVo.setRandomCode(null);
                    }
                    return seckillSkuRelationEntityVo;
                }
            }

        }
        return seckillSkuRelationEntityVo;
    }

    /**
     * 秒杀
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public SeckillSkuRelationEntityVo killSuk(String killId, String key, Integer num) throws NotFindInfoException, SecKillRuleException {
        killId = killId.replace("-", "_");
        BoundHashOperations<String, String, String> boundHashOperations =
                stringRedisTemplate.boundHashOps(SECKILL_CACHE_PREFIX);
        String jsonOb = boundHashOperations.get(killId);
        MemberEntityTo memberEntityTo = SecKillIterceptor.threadLocal.get();
        String memberId = memberEntityTo.getId().toString();
        //抢成功的 key
        String successkey = memberId+"_"+killId;
        Boolean aBoolean = stringRedisTemplate.hasKey(successkey);
        if (aBoolean) {
            throw new SecKillRuleException("本场次没每人人只能参与一次~");
        }
        //没有查询到此产品
        if (!StringUtils.isNotEmpty(jsonOb)) {
            throw new NotFindInfoException("未能查询到产品的秒杀信息~");
        }

        SeckillSkuRelationEntityVo seckillSkuRelationEntityVo =
                JSON.parseObject(jsonOb, SeckillSkuRelationEntityVo.class);

        Long startTime = seckillSkuRelationEntityVo.getStartTime();
        Long endTime = seckillSkuRelationEntityVo.getEndTime();
        long currentTime = new Date().getTime();
        //当前时间不在活动期间内
        if (currentTime < startTime || currentTime > endTime){
            throw new SecKillRuleException("不在活动期间内~");
        }
        String token = seckillSkuRelationEntityVo.getRandomCode();
        //令牌不匹配
        if (!key.equals(token)) {
            throw new SecKillRuleException("令牌不匹配~");
        }
        int count = seckillSkuRelationEntityVo.getSeckillLimit().intValue();
        if (num>count) {
            throw new SecKillRuleException("库存不足，减少购买库存~");
        }
        //消费信号量
        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORES + token);
        try {
            boolean b = semaphore.tryAcquire(100, TimeUnit.MILLISECONDS);
            if (!b) {
                throw new SecKillRuleException("库存不足，请关注后续活动~");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //过期时间
        long dieTime = endTime - currentTime;
        stringRedisTemplate.opsForValue().set(successkey,jsonOb,dieTime,TimeUnit.MILLISECONDS);
        //构建订单
        OrderEntityTo orderEntityTo = new OrderEntityTo();
        orderEntityTo.setMemberId(Long.valueOf(memberId));
        orderEntityTo.setCreateTime(new Date());
        orderEntityTo.setModifyTime(new Date());
        orderEntityTo.setOrderSn(UUID.randomUUID().toString().replace("_", ""));
        orderEntityTo.setPayAmount(seckillSkuRelationEntityVo.getSeckillPrice());
        orderEntityTo.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntityTo.setMemberUsername(memberEntityTo.getUsername());
        orderEntityTo.setSkuId(seckillSkuRelationEntityVo.getSkuId());
        //生产 秒杀订单消息
        rabbitTemplate.convertAndSend(MQConstants.ORDER_EVENT_EXCHANGE,
                MQConstants.SECKILL_ORDER_CREATE,
                orderEntityTo);
        seckillSkuRelationEntityVo.setOrderSn(orderEntityTo.getOrderSn());
        return seckillSkuRelationEntityVo;
    }

}
