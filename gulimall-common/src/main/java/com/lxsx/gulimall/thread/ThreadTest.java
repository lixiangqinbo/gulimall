package com.lxsx.gulimall.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadTest {
   private static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /**
         *  Callable01 callable01 = new Callable01();
         *         FutureTask<Integer> integerFutureTask = new FutureTask<>(callable01);
         *         new Thread(integerFutureTask).start();
         *         Integer integer = integerFutureTask.get();
         *         System.out.println(integer);
         *
         *         ExecutorService service = Executors.newFixedThreadPool(10);
         *         Future<Integer> submit = service.submit(new Callable01());
         *         System.out.println(submit.get());
         * //        ThreadPoolExecutor pool = new ThreadPoolExecutor()
         *         Executors.newCachedThreadPool();// 核心线程为0个 ，最大线程可以很大，但是都会被回收
         *         Executors.newScheduledThreadPool(10);//定时任务的线程池
         *         Executors.newSingleThreadExecutor();//单线程的线程池，后台队列获取任务，挨个执行
         *         Executors.newFixedThreadPool(10);//固定大小 max=core 都不可以回收
         *         CompletableFuture.runAsync(() ->{
         *             System.out.println(Thread.currentThread().getId()+"执行了");
         *         }, service);
         *         CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
         *             System.out.println(Thread.currentThread().getId() + "执行了");
         *             int i = 10 /2;
         *             return i;
         *         }, service).whenComplete((res,exception)->{
         *             System.out.println(res);
         *             System.out.println(exception.getMessage());
         *         }).exceptionally(exception ->{
         *             return 10;
         *         });
         */


        /**
         *
         *  CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
         *             System.out.println(Thread.currentThread().getId() + "执行了");
         *             int i = 10 /4;
         *             System.out.println("i:"+i);
         *             return i;
         *         }, service).whenComplete((res,exception)->{
         *             System.out.println("res:"+res);
         *             System.out.println("exception:"+exception);
         *         }).handle((res,exception) ->{
         *             System.out.println("res::"+res);
         *             System.out.println("exception::"+exception);
         *             if (exception!=null){
         *                 return 50;
         *             }
         *             if (res!=null) {
         *                 return res*10;
         *             }
         *             return 10;
         *         });
         *         Integer integer = integerCompletableFuture.get();
         *         System.out.println("integer:::"+integer);
         *
         */

        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getId() + "号线程任务结束！");
            return 10;
        }, service);

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getId() + "号线程任务结束！");
            return 100;
        }, service);

     /*   future.runAfterBothAsync(future1, ()->{
            System.out.println(Thread.currentThread().getId() + "号线程任务结束！");
        },service);

        future.thenAcceptBothAsync(future1, (f1,f2)->{
            System.out.println(Thread.currentThread().getId() + "号线程任务结束！得到上个结果"+f1+"::"+f2);
        },service);*/
//        CompletableFuture<Integer> integerCompletableFuture = future.thenCombineAsync(future1, (f1, f2) -> {
//            System.out.println(Thread.currentThread().getId() + "号线程任务结束！得到上个结果" + f1 + "::" + f2);
//            return 9999;
//        }, service);
//        System.out.println(integerCompletableFuture.get());
//        future.runAfterEitherAsync(future1, ()->{
//            System.out.println(Thread.currentThread().getId() + "号线程任务结束！");
//            try {
//                System.out.println(future1.get());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }, service);

        List<Integer> integers = Arrays.asList(1, 2, 3, 5, 67);
        List<Integer> collect = integers.stream().filter(integer -> {
            if (integer>10) {
            }
            return integer > 10;
        }).collect(Collectors.toList());
        collect.forEach(System.out::println);
        System.out.println("------------------");
        integers.forEach(System.out::println);
    }


    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {

            return 10/5;
        }
    }
}
