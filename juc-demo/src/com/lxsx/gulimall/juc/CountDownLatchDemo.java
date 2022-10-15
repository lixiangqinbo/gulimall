package com.lxsx.gulimall.juc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 演示实例：班里的5个同学都走了班长关门
 */
public class CountDownLatchDemo {

    private final static int NUMBER = 6;

    public static void main(String[] args) throws InterruptedException {
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(NUMBER);

        for (int i = 0; i <6 ; i++) {
            new Thread(()->{
                try {
                    Thread.sleep(2000);
                    System.out.println(Thread.currentThread().getName()+"同学走了！");
                    //计数减一
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        //等待计数到 0
        countDownLatch.await();
       // countDownLatch.await(3, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName()+"班长关门！");
    }
}
