package com.ctrip.infosec.flowtable4j.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/25.
 */
public class SimpleStaticThreadPool extends ThreadPoolExecutor{
    private static final Logger logger = LoggerFactory.getLogger(SimpleStaticThreadPool.class);

    private SimpleStaticThreadPool(){
        super(64, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new CallerRunsPolicy());
    }

    private static SimpleStaticThreadPool instance = new SimpleStaticThreadPool();
    public static SimpleStaticThreadPool getInstance(){
        return instance;
    }

}
