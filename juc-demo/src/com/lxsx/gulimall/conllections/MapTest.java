package com.lxsx.gulimall.conllections;

import sun.nio.ch.ThreadPool;

import java.util.*;
import java.util.concurrent.*;

public class MapTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        Map<String, String> map = new HashMap<>();
//
//        for (int i = 0; i <30 ; i++) {
//            map.put(String.valueOf(i), UUID.randomUUID().toString().substring(0,8));
//        }
//
//        Map<String,String> map1 = Collections.synchronizedMap(map);
//        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(map);
//        List<String> list = new Vector<>();
//        new Thread(()->{
//            for (int i = 0; i <30 ; i++) {
//                list.add(UUID.randomUUID().toString().substring(0,8));
//            }
//
//        }).start();
//        Thread.sleep(3000);
//        new Thread(()->{
//            for (int i = 0; i <130 ; i++) {
//                System.out.println(list);
//            }
//        }).start();
//        new Thread(()->{
//            for (int i = 0; i <130 ; i++) {
//                list.add(UUID.randomUUID().toString().substring(0,8));
//            }
//
//        }).start();
        //线程池
        ThreadPoolExecutor poolExecutor
                =new ThreadPoolExecutor(10,
                100,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        Future<Integer> submit = poolExecutor.submit(() -> {
            System.out.println("submit()");
            return 100;
        });
        Integer integer = submit.get();
        System.out.println("submit()提交任务带有返回值："+integer);
        poolExecutor.execute(()->{
            System.out.println("execute");
        });

        /**
         * newCachedThreadPool() {
         *         return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
         *                                       60L, TimeUnit.SECONDS,
         *                                       new SynchronousQueue<Runnable>());
         *     }
         */
       Executors.newCachedThreadPool();
        /**
         * new ThreadPoolExecutor(1, 1,
         *  0L, TimeUnit.MILLISECONDS,
         *  new LinkedBlockingQueue<Runnable>())
         */
       Executors.newSingleThreadExecutor();
       Executors.newFixedThreadPool(10);
       Executors.newScheduledThreadPool(10);

    }
}
