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
    private static ThreadPoolExecutor excutor = new ThreadPoolExecutor(64, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticThreadPool.class);
    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,long timeout,TimeUnit timeUnit){
        try {
            return excutor.invokeAll(tasks,timeout,timeUnit);
        } catch (InterruptedException e) {
            logger.error("i am interrupted",e);
            return new ArrayList<Future<T>>();
        }
    }


    public static void shutdown(){
        excutor.shutdown();
    }

    public static void shutdownNow(){
        excutor.shutdownNow();
    }
}
