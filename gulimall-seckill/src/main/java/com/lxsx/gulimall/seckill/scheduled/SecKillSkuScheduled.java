package com.lxsx.gulimall.seckill.scheduled;


import com.lxsx.gulimall.seckill.service.SecKillSkuService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 商品秒杀定时上架
 * 每天定时上架未来3天的商品
 */
@Service
@Slf4j
public class SecKillSkuScheduled {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private SecKillSkuService secKillSkuService;
    /**
     * 分布式锁
     */
    private final String UODATE_LOCK = "seckill:update:lock";

    /**
     * 定时上架最近3天的秒杀产品
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void updateSeckillSkuFuture3days(){
        RLock lock = redissonClient.getLock(UODATE_LOCK);
        try {
            lock.lock();
            secKillSkuService.updateSeckillSkuFuture3days();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
