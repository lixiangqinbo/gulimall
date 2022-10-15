package com.lxsx.gulimall.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * CyclicBarrier：参数1,触发阈值 参数二：触发的业务
 * cyclicBarrier.await();
 *
 */
public class CyclicBarrierDemo {

    private final static int NUMBER = 7;

    public static void main(String[] args) {
        /**
         * 只能唤起一个任务  但是CyclicBarrier可以复用 CountDownLatch不能复用 但是可以唤起多个任务
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER,()->{
            System.out.println(Thread.currentThread().getName()+"召唤神龙！");
        });

        for (int i = 1; i <=7 ; i++) {
            new Thread(()->{

                try {
                    System.out.println(Thread.currentThread().getName()+"集齐了1颗龙珠");
                    cyclicBarrier.await();
                   // cyclicBarrier.await(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }

    }
}
