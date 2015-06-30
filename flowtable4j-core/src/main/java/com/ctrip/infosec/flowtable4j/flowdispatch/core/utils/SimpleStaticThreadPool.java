package com.ctrip.infosec.flowtable4j.flowdispatch.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/25.
 */
public class SimpleStaticThreadPool extends ThreadPoolExecutor{
    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticThreadPool.class);

    private SimpleStaticThreadPool(){
        super(64, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private static SimpleStaticThreadPool instance = new SimpleStaticThreadPool();
    public static SimpleStaticThreadPool getInstance(){
        return instance;
    }
//    protected void afterExecute(Runnable r, Throwable t) {
//        super.afterExecute(r, t);
//        Future<?> f = (Future<?>) r;
//        try {
//            f.get();
//        } catch (InterruptedException e) {
//            logger.error("线程池中发现异常，被中断 ,InterruptedException");
//        } catch (ExecutionException e) {
//            logger.error("线程池中发现异常", e);
//        }
//    }
}
