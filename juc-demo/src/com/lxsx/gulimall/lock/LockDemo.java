package com.lxsx.gulimall.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程的定制化通信
 * ReentrantLock
 *
 */
class Print{
    private int flag =1;
    private Lock lock = new ReentrantLock();

    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    Condition c3 = lock.newCondition();


    public void  print5(int loop) throws InterruptedException {
            lock.lock();
            try{
                while (flag!=1){
                    c1.await();
                }
                for (int i = 1; i <=loop ; i++) {
                    System.out.println(Thread.currentThread().getName()+"打印第"+i+"次");
                }
                flag =2;
                c2.signal();
            }finally {
                lock.unlock();
            }

    }
    public void  print10(int loop) throws InterruptedException {
        lock.lock();
        try{
            while (flag!=2){
                c2.await();
            }
            for (int i = 1; i <=loop ; i++) {
                System.out.println(Thread.currentThread().getName()+"打印第"+i+"次");
            }
            flag =3;
            c3.signal();
        }finally {
            lock.unlock();
        }

    }
    public void  print15(int loop) throws InterruptedException {
        lock.lock();
        try{
            while (flag!=3){
                c3.await();
            }
            for (int i = 1; i <=loop ; i++) {
                System.out.println(Thread.currentThread().getName()+"打印第"+i+"次");
            }
            flag =1;
            c1.signal();
        }finally {
            lock.unlock();
        }

    }

}
public class LockDemo {
   static Print print =new Print();
    public static void main(String[] args) {
        new Thread(()->{
            try {
                for (int i = 0; i <10 ; i++) {
                    print.print5(5);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"AA").start();

        new Thread(()->{
            try {
                for (int i = 0; i <10 ; i++) {
                    print.print10(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"BB").start();

        new Thread(()->{
            try {
                for (int i = 0; i <10 ; i++) {
                    print.print15(15);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"CC").start();
    }
}


