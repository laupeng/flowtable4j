package com.ctrip.flowtable4j.core;

import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.model.account.AccountCheckItem;
import com.ctrip.infosec.flowtable4j.model.check.CheckEntity;
import com.ctrip.infosec.flowtable4j.model.check.CheckType;
import com.ctrip.infosec.flowtable4j.model.check.RiskResult;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    public List<RiskResult> handle(final CheckEntity checkEntity) {
        final List<RiskResult> listResult_w = new ArrayList<RiskResult>();
        final List<RiskResult> listResult_b = new ArrayList<RiskResult>();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<RiskResult> listResult = new ArrayList<RiskResult>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        /**
         * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和flowrule
         */
        for(CheckType type : checkEntity.getCheckTypes()){
            if(type==CheckType.BW){
                if(BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)){
                    return listResult_w;
                }
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                    }
                });
            }
        }



        for(CheckType type : checkEntity.getCheckTypes()){
            if(type==CheckType.ACCOUNT){
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<AccountCheckItem> list = new ArrayList<AccountCheckItem>();
                        AccountCheckItem item = checkEntity.getAccountCheckItem();
                        list.add(item);
                        paymentViaAccount.CheckBWGRule(list,mapAccount);
                    }
                });
            }
            if(type==CheckType.FLOWRULE){
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO flowrule
                    }
                });
            }
        }
        executorService.shutdown();
        try {
            if(executorService.awaitTermination(TIMEOUT,TimeUnit.MILLISECONDS)){
                logger.info("***awaitTermination return true***");
                for(Iterator<String> it=mapAccount.keySet().iterator();it.hasNext();){
                    String sceneType = it.next();
                    RiskResult riskResult = new RiskResult();
                    listResult.add(riskResult);
                    riskResult.setRuleType(CheckType.ACCOUNT.toString());
                    riskResult.setRuleName(sceneType);
                    riskResult.setRiskLevel(mapAccount.get(sceneType));
                }
                listResult.addAll(listResult_b);
            }else {
                executorService.shutdownNow();
                logger.info("在100ms内没有完成任务,强制关闭");
            }
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+"is interrupted",e);
        }
        /**
         * Account 整合
         */

        return listResult;
    }

}
