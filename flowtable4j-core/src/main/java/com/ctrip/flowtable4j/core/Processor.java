package com.ctrip.flowtable4j.core;
import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.model.account.AccountCheckItem;
import com.ctrip.infosec.flowtable4j.model.bw.BWResult;
import com.ctrip.infosec.flowtable4j.model.check.CheckEntity;
import com.ctrip.infosec.flowtable4j.model.check.CheckType;
import com.ctrip.infosec.flowtable4j.model.check.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

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
        final List<BWResult> listResult_w = new ArrayList<BWResult>();
        final List<BWResult> listResult_b = new ArrayList<BWResult>();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<RiskResult> listResult = new ArrayList<RiskResult>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        /**
         * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和flowrule
         */
        for(CheckType type : checkEntity.getCheckTypes()){
            if(type==CheckType.BW){
                if(BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)){

                    return listResult;
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

        try {
            executorService.awaitTermination(TIMEOUT,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+"is interrupted",e);
        }

        return listResult;
    }

    private void handle4Account(final CheckEntity checkEntity, ExecutorService executorService) {
        for(CheckType type : checkEntity.getCheckTypes()){
            if(type==CheckType.ACCOUNT){
                List<AccountCheckItem> list = new ArrayList<AccountCheckItem>();
                Map<String, Integer> result = new HashMap<String, Integer>();
                AccountCheckItem item = checkEntity.getAccountCheckItem();
                list.add(item);
                paymentViaAccount.CheckBWGRule(list,result);
                logger.info(">>> account");
                for(Iterator<String> it = result.keySet().iterator();it.hasNext();){
                    String key = it.next();
                    logger.info(key+":"+result.get(key));
                }
                logger.info("<<<");
            }
        }
    }

    private void handle4BW(final CheckEntity checkEntity, ExecutorService executorService) {
        final List<BWResult> listResult_w = new ArrayList<BWResult>();
        final List<BWResult> listResult_b = new ArrayList<BWResult>();
//        Future<Boolean> futureWhite = null;
        for (CheckType type : checkEntity.getCheckTypes()) {
            if (type == CheckType.BW) {
                if(BWManager.checkWhite(checkEntity.getBwFact(), listResult_w)){
                    return ;
                }
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                    }
                });
            }
        }
    }

    private void handle4Payment(CheckEntity checkEntity, ExecutorService executorService) {

    }
}
