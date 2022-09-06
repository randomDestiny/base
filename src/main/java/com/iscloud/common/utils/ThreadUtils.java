package com.iscloud.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @Desc: 线程工具类
 * @Author: HYbrid
 * @Date: 2022/1/4 14:29
 */
@SuppressWarnings("unused")
public class ThreadUtils extends org.apache.commons.lang3.ThreadUtils {
    public static final String BEAN = "ThreadUtils";

    public static Executor buildThreadPoolTask(String name, int size) {
        String n = StringUtils.isNotBlank(name) ? name : System.currentTimeMillis() + "";
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(size);
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(100);
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(500);
        // 允许线程的空闲时间60秒：当超过了核心线程之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix(n);
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    public static Executor buildThreadPoolTask(String name) {
        return buildThreadPoolTask(name, 20);
    }

    public static void run(Runnable task) {
        run(task, 1);
    }

    public static void run(Runnable task, int poolSize) {
        Executors.newFixedThreadPool(poolSize > 0 ? poolSize : 1).submit(task);
    }

    /**
     * @Desc:   异步
     * @Params: [supplier]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/4
     */
    public static <U> void async(Supplier<U> supplier) {
        CompletableFuture.supplyAsync(supplier);
    }

    /**
     * @Desc:   异步
     * @Params: [supplier, poolSize]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/4
     */
    public static <U> void async(Supplier<U> supplier, int poolSize) {
        CompletableFuture.supplyAsync(supplier, Executors.newFixedThreadPool(poolSize > 0 ? poolSize : 1));
    }

    /**
     * @Desc:   异步
     * @Params: [runnable]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/4
     */
    public static void async(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    /**
     * @Desc:   异步
     * @Params: [runnable, poolSize]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/1/4
     */
    public static void async(Runnable runnable, int poolSize) {
        CompletableFuture.runAsync(runnable, Executors.newFixedThreadPool(poolSize > 0 ? poolSize : 1));
    }

}
