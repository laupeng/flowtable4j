package com.ctrip.infosec.flowtable4j.jobws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by zhangsx on 2015/3/13.
 * 规则更新实现类
 */
@Component
public class UpdaterManager {

    @Autowired
    private BWRuleUpdater bwUpdater;

    @Autowired
    private FlowRuleUpdater flowUpdater;

    @Autowired
    private PayAdaptRuleUpdater payAdaptUpdater;

    private static final Logger logger = LoggerFactory.getLogger(UpdaterManager.class);

    @Scheduled(fixedDelay = 300000)
    public void executeBW() {
        logger.info("start execute update blackWhite rule...");
        try {
            bwUpdater.execute();
        } catch (Throwable ex) {
            logger.error("bwUpdater error.",ex);
        }
        logger.info("end execute update blackWhite rule...");
    }

    @Scheduled(fixedDelay = 300000)
    public void executeFlow() {
        logger.info("start execute update flow rule...");
        try {
            flowUpdater.execute();
        } catch (Throwable ex) {
            logger.error("flowUpdater error",ex);
        }
        logger.info("end execute update flow rule...");
    }

    @Scheduled(fixedDelay = 300000)
    public void executePayadapt(){
        logger.info("start execute update blackWhite rule...");
        try {
            payAdaptUpdater.execute();
        }catch (Throwable ex){
            logger.error("payAdaptUpdater error",ex);
        }

        logger.info("end execute update payAdapt rule...");
    }
}
