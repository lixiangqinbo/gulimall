import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.*;

public class ThreadTest {
    /**
     * 自定义线程池
     */
   static final ThreadPoolExecutor executor = new org.apache.tomcat.util.threads.ThreadPoolExecutor(20,
            200,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 不带返回值的异步请求，可以自定义线程池
     * public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
     */
    @Test
    public void test_runAsync(){
        System.out.println("d");
        CompletableFuture.runAsync(()->{
            System.out.println("runAsync无返回值！");
        }, executor);

    }

    /**
     * 带返回值的异步请求，可以自定义线程池
     * public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
     */
    @Test
    public void test_supplyAsync(){
        System.out.println("d");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("runAsync无返回值！");
            return 10 * 10;
        }, executor);

    }

    /**
     * 如果完成则返回结果，否则就抛出具体的异常
     * public T get() throws InterruptedException, ExecutionException
     *
     * 最大时间等待返回结果，否则就抛出具体异常
     * public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
     *
     * 完成时返回结果值，否则抛出unchecked异常。为了更好地符合通用函数形式的使用，如果完成此 CompletableFuture所涉及的计算引发异常，则此方法将引发unchecked异常并将底层异常作为其原因
     * public T join()
     *
     * 如果完成则返回结果值（或抛出任何遇到的异常），否则返回给定的 valueIfAbsent。
     * public T getNow(T valueIfAbsent)
     *
     * 如果任务没有完成，返回的值设置为给定值
     * public boolean complete(T value)
     *
     * 如果任务没有完成，就抛出给定异常
     * public boolean completeExceptionally(Throwable ex)
     *
     */
    @Test
    public void fetch() throws ExecutionException, InterruptedException, TimeoutException {
        int num1 =10;
        int num2 = 10;
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("runAsync返回值！");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return num1 * num2/0;
        }, executor);
        //如果完成则返回结果值（或抛出任何遇到的异常），否则返回给定的 valueIfAbsent
        Integer now = future.getNow(99);
        System.out.println("now:"+now);
        //如果任务没有完成，返回的值设置为给定值
        boolean complete = future.complete(88);
        Integer integer2 = future.get();
        System.out.println("integer2:"+integer2);
        //如果任务没有完成，就抛出给定异常
        future.completeExceptionally(new RuntimeException());
        //如果完成则返回结果，否则就抛出具体的异常
        Integer integer = future.get();
        System.out.println("integer:"+integer);
        //最大时间等待返回结果，否则就抛出具体异常
        Integer integer1 = future.get(3, TimeUnit.SECONDS);
        System.out.println("integer1:"+integer1);
        //完成时返回结果值，否则抛出unchecked异常
        Integer join = future.join();
        System.out.println("join:"+join);
    }

    /**
     * thenAcceptAsync：没返回值
     * thenApplyAsync：有返回值
     * thenRunAsync：上个任务执行完的回调
     */
    @Test
    public void test_then(){
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return 100;
        }, executor);
        future1.thenAcceptAsync((f1)->{
            System.out.println("接收发f1的返回值,自己没有返回值："+f1);
                }, executor);
        CompletableFuture<Integer> future = future1.thenApplyAsync((f1) -> {
            return f1 * 2;
        }, executor);
        Integer join = future.join();
        System.out.println("接收发f1的返回值并处理后返回："+join);

        future1.thenRunAsync(()->{
            System.out.println("上个任务执行完的回调");
        }, executor);
    }

    /**
     * whenCompleteAsync：当某个任务完成的回调方法：会传递上个任务的结果和异常
     * 如果有异常则结果为null，如果有结果则异常为null
     * 使用场景：感觉可以用来处理异常
     * handleAsync:再whenCompleteAsync的基础上多个返回值
     */
    @Test
    public void test_when_handle() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return 100;
        }, executor);

        CompletableFuture<Integer> future = future1.whenCompleteAsync((f1, exception) -> {
            System.out.println("future1传递的参数：" + f1);
            System.out.println("传递的异常：" + exception);
        }, executor);
        Integer integer = future.get();
        /** return 100;
         * future1传递的参数：100
         * 传递的异常：null
         * integer:100
         *
         * return 100/0;
         * future1传递的参数：null
         * 传递的异常：java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
         */
        System.out.println("integer:"+integer);
        /**return 100;
         * future1传递的参数：100
         * 传递的异常：null
         * integer1:1000
         *
         *return 100/0;
         *future1传递的参数：null
         * 传递的异常：java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
         */
        CompletableFuture<Integer> future2 = future1.handleAsync((f1, exception) -> {
            System.out.println("future1传递的参数：" + f1);
            System.out.println("传递的异常：" + exception);
            return f1 * 10;
        }, executor);
        Integer integer1 = future2.get();
        Integer integer2 = future.get();
        System.out.println("integer1:"+integer1);
        System.out.println("integer2:"+integer2);
    }

    @Test
    public void test_combine_both() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return 100;
        }, executor);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return 100;
        }, executor);
        /**
         * 合并两个任务：接收两个任务的参数 并带有返回值
         */
        CompletableFuture<Integer> future = future1.thenCombineAsync(future2, (f1, f2) -> {
            return f1 + f2;
        }, executor);
        Integer integer = future.get();
        System.out.println("integer:"+integer);
        /**
         * 接收两个任务的参数，无返回值
         * 使用场景C表依赖A B的结果，但无需返回结果
         * C等两个执行完后 再去CRUD
         */
        future1.thenAcceptBothAsync(future2, (f1,f2)->{
            System.out.println("接收两个任务的参数，无返回值");
        }, executor);
        /**
         * 两个方法都结束的回调
         */
        future1.runAfterBothAsync(future2, ()->{
            System.out.println("无参，无返回值");
        }, executor);
    }

    /**
     * 组合两个任务
     * 其中一个完成就执行接下来的任务
     * applyToEitherAsync：入参(先结束的任务) 返回值
     * acceptEitherAsync：入参(先结束的任务) 无返回值
     * runAfterEitherAsync：无参 无返回；单个完成后的回调
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test_either() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 200;
        }, executor);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return 100;
        }, executor);

        CompletableFuture<Integer> future = future1.applyToEitherAsync(future2, (f) -> {
            System.out.println("future2先完成接f2的结果:" + f);
            return f;
        }, executor);
        Integer integer = future.get();
        System.out.println("integer:"+integer);

        future1.runAfterEitherAsync(future2, ()->{
            System.out.println("任意完成一个就回调这个方法:无出参 无入参");
        }, executor);

        future1.acceptEitherAsync(future2, (f)->{
            System.out.println("future2先完成接f2的结果无返回值:" + f);
        },executor);
    }
}
