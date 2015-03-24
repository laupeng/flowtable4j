package com.ctrip.infosec.flowtable4j.jobws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/13.
 * 规则更新实现类
 */
@Component
public class StaticCacheJob {

    @Autowired
    @Qualifier("simpleProcessor4BW")
    private Processer processerBW;

    @Autowired
    @Qualifier("simpleProcessor4Flow")
    private Processer processerFlow;

    private static final Logger logger = LoggerFactory.getLogger(StaticCacheJob.class);
    @Scheduled(fixedDelay = 60000)
    public void executeBW(){
        logger.info("start execute update bw rule...");
        processerBW.execute();
        logger.info("end execute update bw rule...");
    }

    @Scheduled(fixedDelay = 10000)
    public void executeFlow(){
        logger.info("start execute update flow rule...");
        processerFlow.execute();
        logger.info("end execute update flow rule...");
    }
}
