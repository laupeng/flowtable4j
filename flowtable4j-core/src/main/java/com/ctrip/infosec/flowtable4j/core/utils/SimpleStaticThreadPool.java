package com.ctrip.infosec.flowtable4j.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/25.
 */
public class SimpleStaticThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticThreadPool.class);
    private static ThreadPoolExecutor excutor = new InnerThreadPool(64, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit timeUnit) {
        try {
            return excutor.invokeAll(tasks, timeout, timeUnit);
        } catch (InterruptedException e) {
            logger.error("i am interrupted", e);
            return new ArrayList<Future<T>>();
        }
    }


    public static void shutdown() {
        excutor.shutdown();
    }

    public static void shutdownNow() {
        excutor.shutdownNow();
    }

    protected static class InnerThreadPool extends ThreadPoolExecutor {

        public InnerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public InnerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public InnerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public InnerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            Future<?> f = (Future<?>) r;
            try {
                f.get();
            } catch (InterruptedException e) {
                logger.error("线程池中发现异常，被中断 ,InterruptedException");
            } catch (ExecutionException e) {
                logger.error("线程池中发现异常，被中断", e);
            }
        }
    }



}
