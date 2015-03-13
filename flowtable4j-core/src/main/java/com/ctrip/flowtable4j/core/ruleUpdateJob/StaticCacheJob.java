package com.ctrip.flowtable4j.core.ruleUpdateJob;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/13.
 * 规则更新实现类
 */
public class StaticCacheJob implements RuleSwitch{

    /**
     * 规则更新Timer
     */
    private ScheduledExecutorService executor = null;
    @Autowired
    private Processer processer;
    @Override
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                processer.execute();
            }
        },0L,5L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        if(executor!=null){
            executor.shutdown();
        }
    }

    @Override
    public void shutdownNow() {
        if(executor!=null){
            executor.shutdownNow();
        }
    }
}
