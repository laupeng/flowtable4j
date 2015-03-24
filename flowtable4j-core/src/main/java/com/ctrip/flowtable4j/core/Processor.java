package com.ctrip.flowtable4j.core;

import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.model.bw.BWResult;
import com.ctrip.infosec.flowtable4j.model.check.CheckEntity;
import com.ctrip.infosec.flowtable4j.model.check.CheckType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhangsx on 2015/3/24.
 */
public class Processor {
    private static Logger logger = LoggerFactory.getLogger(Processor.class);
    private static final long TIMEOUT = 100;
    public static void handle(CheckEntity checkEntity) {
        checkEntity.getCheckTypes();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
            executorService.awaitTermination(TIMEOUT,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+"is interrupted",e);
        }
    }

    private static void handle4Account() {

    }

    private static void handle4BW(final CheckEntity checkEntity, ExecutorService executorService) {
        final List<BWResult> listResult = new ArrayList<BWResult>();
        Future<Boolean> futureWhite = null;
        for (CheckType type : checkEntity.getCheckTypes()) {
            if (type == CheckType.BW) {
                futureWhite = executorService.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return BWManager.checkWhite(checkEntity.getBwFact(), listResult);
                    }
                });

                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult);
                    }
                });
            }
        }
        try {
            if (futureWhite != null && futureWhite.get().booleanValue()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName() + "is interrupted", e);
        } catch (ExecutionException e) {
            logger.error("error.", e);
        }
    }

    private static void handle4Payment(CheckEntity checkEntity, ExecutorService executorService) {

    }
}
