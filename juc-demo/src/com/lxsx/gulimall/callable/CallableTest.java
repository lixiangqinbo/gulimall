package com.lxsx.gulimall.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class MyThread1 implements Runnable{

    @Override
    public void run() {

    }
}

class MyThread2 implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 0; i <50 ; i++) {
            sum+=i;
        }
        return sum;
    }
}

/**
 * Callable 创建线程：
 * 1。实现Callable接口
 * 2. 将接口 --> FutureTask
 * new Thread(Callable)
 *
 * 特点:Callable 计算结果后只会计算一次，当下次在执行就直接拿结果，相当于执行完了后会
 * 缓存计算结果，之后不管多少次futureTask.get()； 都不会在去计算，而是直接返回结果
 */
public class CallableTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask futureTask = new FutureTask(new MyThread2());
        FutureTask futureTask1 = new FutureTask(()->{
            int sum = 0;
            for (int i = 0; i <50 ; i++) {
                System.out.println("sum:"+sum);
                sum+=i;
            }
            return sum;

        });

        new Thread(futureTask1, "BB").start();
        Integer o1 = (Integer) futureTask1.get();
        System.out.println(o1);
        Integer o2 = (Integer) futureTask1.get();
        System.out.println(o2);


    }
}
