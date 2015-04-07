package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.flowlist.FlowRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/24.
 */
@Component
public class Processor {
    private static Logger logger = LoggerFactory.getLogger(Processor.class);
    @Autowired
    private PaymentViaAccount paymentViaAccount;
    private static final long TIMEOUT = 100;

    public RiskResult handle(final CheckFact checkEntity) {
        final RiskResult listResult_w = new RiskResult();
        RiskResult listResult = new RiskResult();
        try {
            boolean isWhite = false;
            /**
             * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和flowrule
             */
            for (CheckType type : checkEntity.getCheckTypes()) {
                if (type == CheckType.BW) {
                    if (BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)) {
                        listResult.merge(listResult_w);
                        isWhite = true;
                    }
                }
            }
            if (!isWhite) {
                parallelCheck(checkEntity, listResult);
            }
        } catch (Throwable ex) {
            listResult.setStatus("FAIL");
            logger.error("error.",ex);
        }
        return listResult;
    }

    private void parallelCheck(final CheckFact checkEntity, RiskResult listResult) {
        final RiskResult listResult_b = new RiskResult();
        final RiskResult listFlow = new RiskResult();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        CheckType[] checkTypes = checkEntity.getCheckTypes();
        for(CheckType type : checkTypes){
            if(CheckType.BW==type){
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.currentTimeMillis();
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                        logger.info("***1:" + (System.currentTimeMillis() - now));
                        return null;
                    }
                });
            }else if(CheckType.ACCOUNT==type){
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.currentTimeMillis();
                        AccountFact item = checkEntity.getAccountFact();
                        paymentViaAccount.CheckBWGRule(item, mapAccount);
                        logger.info("***2:" + (System.currentTimeMillis() - now));
                        return null;
                    }
                });
            }else if(CheckType.FLOWRULE==type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() {
                        long now = System.currentTimeMillis();
                        FlowFact flowFact = checkEntity.getFlowFact();
                        FlowRuleManager.check(flowFact, listFlow);
                        logger.info("***3:" + (System.currentTimeMillis() - now));
                        return null;
                    }
                });
            }
        }
        List<Future<Object>> futures = SimpleStaticThreadPool.invokeAll(tasks, 1000, TimeUnit.MILLISECONDS);
        for (Iterator<String> it = mapAccount.keySet().iterator(); it.hasNext(); ) {
            String sceneType = it.next();
            CheckResultLog riskResult = new CheckResultLog();
            riskResult.setRuleType(CheckType.ACCOUNT.toString());
            riskResult.setRuleName(sceneType);
            riskResult.setRiskLevel(mapAccount.get(sceneType));
            listResult.add(riskResult);
        }
        listResult.merge(listResult_b);
        listResult.merge(listFlow);
    }

}
