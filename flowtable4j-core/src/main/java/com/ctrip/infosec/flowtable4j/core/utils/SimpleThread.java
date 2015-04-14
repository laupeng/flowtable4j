package com.ctrip.infosec.flowtable4j.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangsx on 2015/4/9.
 */
public class SimpleThread extends Thread{
    private static Logger logger = LoggerFactory.getLogger(SimpleThread.class);
    private Runnable r ;
    public SimpleThread(Runnable r){
        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("threadpool exception",e);
            }
        });
        this.r=r;
    }

    @Override
    public void run() {
        r.run();
    }
}
