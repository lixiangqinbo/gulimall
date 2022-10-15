package com.lxsx.gulimall.seckill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//
//@EnableScheduling//开启定时任务
//@Component
//@Slf4j
//@EnableAsync //开启异步任务
public class SecKillServiceImpl {


    /**
     *  cron表达式：* * * * * ?对应 秒 分 时 日 月 周
     *    周和日 要模糊有一个就要为 ？
     *    定时任务你应该被阻塞：默认时阻塞的
     *    1》可以用异步编排实现异步执行
     *    2》支持定时任务线程池：
     *          TaskSchedulingProperties
     *          spring.task.scheduling.size=10 默认等于1 所以会阻塞
     *    3》让定时任务异步执行
     *      @EnableAsync //开启异步任务
     *      异步任务：方法上注解@Asyns
     *      自动配置类再TaskExecutionAutoConfiguration->TaskExecutionProperties
     *  task:
     *     execution:
     *       pool:
     *         core-size: 20
     *         max-size: 200
     *     最终解决：定时任务不阻塞
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
    public void test() throws InterruptedException {
        Thread.sleep(3000);
       // log.info("Scheduled....");
    }
}
