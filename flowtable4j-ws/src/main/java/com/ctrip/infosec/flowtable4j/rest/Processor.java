package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.flowtable4j.core.utils.SimpleStaticThreadPool;
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
    @Autowired
    private SimpleStaticThreadPool simpleStaticThreadPool;
    private static final long TIMEOUT = 100;
    public List<RiskResult> handle(final CheckFact checkEntity) {
        final List<RiskResult> listResult_w = new ArrayList<RiskResult>();
        final List<RiskResult> listResult_b = new ArrayList<RiskResult>();
        final List<RiskResult> listFlow = new ArrayList<RiskResult>();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<RiskResult> listResult = new ArrayList<RiskResult>();
        /**
         * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和flowrule
         */
        for(CheckType type : checkEntity.getCheckTypes()){
            if(type==CheckType.BW){
                if(BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)){
                    return listResult_w;
                }
            }
        }

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        tasks.add(new Callable() {
            @Override
            public Object call() throws Exception {
                BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                return null;
            }
        });
        tasks.add(new Callable() {
            @Override
            public Object call() throws Exception {
                AccountFact item = checkEntity.getAccountFact();
                paymentViaAccount.CheckBWGRule(item,mapAccount);
                return null;
            }
        });

        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                FlowFact flowFact = checkEntity.getFlowFact();
                FlowRuleManager.check(flowFact,listFlow);
                return null;
            }
        });
        List<Future<Object>> futures = simpleStaticThreadPool.invokeAll(tasks, 2, TimeUnit.SECONDS);
        for(Future future:futures){
            if(!future.isDone()){
                future.cancel(true);
            }
        }

        for(Iterator<String> it=mapAccount.keySet().iterator();it.hasNext();){
            String sceneType = it.next();
            RiskResult riskResult = new RiskResult();
            listResult.add(riskResult);
            riskResult.setRuleType(CheckType.ACCOUNT.toString());
            riskResult.setRuleName(sceneType);
            riskResult.setRiskLevel(mapAccount.get(sceneType));
        }
        listResult.addAll(listResult_b);
        listResult.addAll(listFlow);
        return listResult;
    }

}
