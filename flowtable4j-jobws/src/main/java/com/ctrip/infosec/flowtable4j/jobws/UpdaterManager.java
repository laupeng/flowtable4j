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
    TableInfoUpdater tableInfoUpdater;

    private static final Logger logger = LoggerFactory.getLogger(UpdaterManager.class);

    @Scheduled(fixedDelay = 300000)
    public void executeBW() {
        logger.info("start execute update blackWhite rule...");
        try {
            bwUpdater.execute();
        } catch (Throwable ex) {
            logger.error("bwUpdater error.", ex);
        }
        logger.info("end execute update blackWhite rule...");
    }

    @Scheduled(fixedDelay = 300000)
    public void executeTableInfo() {
        logger.info("start execute update ColumnInfo...");
        try {
            tableInfoUpdater.execute();
        } catch (Throwable ex) {
            logger.error("tableInfo updater error", ex);
        }

        logger.info("end execute update ColumnInfo...");
    }

}
