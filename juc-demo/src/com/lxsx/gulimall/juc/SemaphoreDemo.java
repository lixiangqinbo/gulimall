package com.lxsx.gulimall.juc;


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号量模拟占车位
 * Semaphore 初始信号量
 * emaphore.acquire();
 * semaphore.release();
 * boolean b = semaphore.tryAcquire(3, TimeUnit.SECONDS);
 */
public class SemaphoreDemo {

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(3);

        for (int i = 1; i <=6 ; i++) {
            new Thread(()->{
                try {
                    //尝试获取信号量等带指定时间 返回占用结果
                    //semaphore.acquire();阻塞等待
                    boolean b = semaphore.tryAcquire(3, TimeUnit.SECONDS);
                    if (b) {
                        System.out.println(Thread.currentThread().getName()+"占到了车位");
                    }else {
                        System.out.println(Thread.currentThread().getName()+"没车位 不停了 走了");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println(Thread.currentThread().getName()+"开走了");
                    //释放信号量
                    semaphore.release();
                }

            },String.valueOf(i)).start();
        }
    }
}
