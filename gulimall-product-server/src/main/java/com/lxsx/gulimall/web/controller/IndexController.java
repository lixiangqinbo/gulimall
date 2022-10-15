package com.lxsx.gulimall.web.controller;

import com.lxsx.gulimall.product.entity.CategoryEntity;
import com.lxsx.gulimall.product.service.CategoryService;
import com.lxsx.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redisson;

    @GetMapping({"/","/index","index.html"})
    public String index(Model model){
        List<CategoryEntity> categoryEntities = categoryService.queryLeve1Catalog();
        model.addAttribute("category", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String,List<Catelog2Vo>> getCatelog(){
        Map<String, List<Catelog2Vo>> catelogsTree = categoryService.getCatelogsTree();

        return catelogsTree;
    }

    /**
     * 可重入锁
     */
    @ResponseBody
    @GetMapping("/redisson")
    public String redisson(){
        //1. 获取锁
        RLock lock = redisson.getLock("my-lock");
        // 2. 加锁
        //lock.lock( );//阻塞等待 默认过期时间是看门狗的时间30S，如果没有设置过期时间就是LockWatchDogTimeout的默认过期时间就是30s
        //只要占锁成功，就会启动一个定时认任务器，每间隔10S会给锁自动续期LockWatchDogTimeout/3的时间 也就10s
        /**
         * 上锁时就设置过期时间。到时候自动解锁。如果过期时间内业务还没运行完，解锁就会报错
         * 2022-09-23 17:52:24.140  INFO 15656 --- [nio-8083-exec-2] c.l.g.e.GlobalExceptionControllerAdvice  : attempt to unlock lock,
         * not locked by current thread by node id: 7a485a97-37bb-4742-a8e6-ca9cb6a130cb thread-id: 405::class java.lang.IllegalMonitorStateException
         *
         */
        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println(Thread.currentThread().getId()+"号线程加锁成功！执行业务....");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //3. 释放锁
            lock.unlock();
            System.out.println(Thread.currentThread().getId()+"锁释放成功！");
        }
        return "redisson";
    }


        /**
         * 读写锁 测试
         */
     @ResponseBody
     @GetMapping("/r")
     public  String rlock(){
         RReadWriteLock rlock = redisson.getReadWriteLock("r-w-lock");
         RLock lock = rlock.readLock();
         String uuid ="";
         lock.lock();
         try{ uuid = stringRedisTemplate.opsForValue().get("uuid");
         }catch (Exception E){

         }finally {
             lock.unlock();
         }

         return uuid;
     }


    @ResponseBody
    @GetMapping("/w")
    public String wlock(){
        RReadWriteLock lock = redisson.getReadWriteLock("r-w-lock");
        RLock wLock = lock.writeLock();
        wLock.lock();
        try {
            String uuid = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("uuid",uuid);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            wLock.unlock();
        }

        return "写入完成！";
    }

    /**
     *  信号量（Semaphore）
     * 基于Redis的Redisson的分布式信号量（Semaphore）Java对象RSemaphore
     * 采用了与java.util.concurrent.Semaphore相似的接口和用法。
     * 同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口。
     */
    //可以用作分布式限流
    @ResponseBody
    @GetMapping("/park")
    public String park(){
        RSemaphore rSemaphore = redisson.getSemaphore("park");
        try {
            rSemaphore.acquire(1); //获取一个信号量，阻塞等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "停车！！";
    }

    @ResponseBody
    @GetMapping("/go")
    public String gogo(){
        RSemaphore rSemaphore = redisson.getSemaphore("park");
        rSemaphore.release();//释放一个信号
        return "车开走了！！";
    }

    /**
     * 闭锁（CountDownLatch）
     * 放假 锁门
     * 5个班全部走了才锁门
     */
    @ResponseBody
    @GetMapping("/gogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch latch = redisson.getCountDownLatch("door");
        latch.countDown();
        return id+"班学生都走了！！";
    }

    @ResponseBody
    @GetMapping("/door")
    public String door(){
        RCountDownLatch latch = redisson.getCountDownLatch("door");
        latch.trySetCount(5);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "大门关闭！";
    }
}
