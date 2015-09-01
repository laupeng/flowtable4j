package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.accountrule.AccountBWGManager;
import com.ctrip.infosec.flowtable4j.bwrule.BWManager;
import com.ctrip.infosec.flowtable4j.dal.CardRiskDbService;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.sars.monitor.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/5/19.
 */
@Component
public class FlowtableProcessor {

    private static Logger logger = LoggerFactory.getLogger(FlowtableProcessor.class);

    @Autowired
    private BWManager bwManager;

    @Autowired
    private AccountBWGManager accountBWGManager;

    @Autowired
    private CardRiskDbService cardRiskDbService;

    private static final long FLOWTIMEOUT = 10000;

     /**
     * 支付校验
     *
     * @param checkEntity
     * @return
     */
    public RiskResult handle(final CheckFact checkEntity) {

        long s = System.nanoTime();
        logger.debug(Utils.JSON.toJSONString(checkEntity));
        logger.debug(String.format("ReqId %d to Json elapsed %d", checkEntity.getReqId(), (System.nanoTime() - s)/1000000L));

        final RiskResult listResult_w = new RiskResult();
        final RiskResult listResult = new RiskResult();

        boolean isWhite = false;
        /**
         * 1. 检测是否是白名单，是就直接返回，否则继续check黑名单，账户和 Flow Rule
         */
        for (CheckType type : checkEntity.getCheckTypes()) {
            if (type == CheckType.BW) {
                if (bwManager.checkWhite(checkEntity.getBwFact(), listResult_w)) {
                    listResult.merge(listResult_w);
                    isWhite = true;
                }
                long eps = (System.nanoTime() - s) /1000000L;
                String info = String.format("ReqId:%d, CheckWhite elapse %d ms", checkEntity.getReqId(), eps);
                logger.debug(info);
            }
        }
        if (!isWhite) {
            parallelCheck(checkEntity, listResult);
        }
        listResult.setReqId(checkEntity.getReqId());
        return listResult;
    }

    private void parallelCheck(final CheckFact checkEntity, RiskResult listResult) {
        final RiskResult listResult_b = new RiskResult();
        final RiskResult listFlow = new RiskResult();
        final Map<String, Integer> mapAccount = new HashMap<String, Integer>();
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        CheckType[] checkTypes = checkEntity.getCheckTypes();
        for (CheckType type : checkTypes) {
            if (CheckType.BW == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.nanoTime();
                        bwManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                        long eps = (System.nanoTime() - now)/1000000L;
                        String info = String.format("ReqId:%d,CheckBlack elapse %d ms", checkEntity.getReqId(), eps);
                        logger.debug(info);
                        return null;
                    }
                });
            } else if (CheckType.ACCOUNT == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.nanoTime();
                        AccountFact item = checkEntity.getAccountFact();
                        if (item != null && item.getCheckItems() != null && item.getCheckItems().size() > 0) {
                            accountBWGManager.checkBWGRule(item, mapAccount);
                        }
                        long eps = (System.nanoTime() - now) /1000000L;
                          String info = String.format("ReqId:%d,CheckBWGRule elapse %d ms", checkEntity.getReqId(), eps);
                        logger.debug(info);
                        return null;
                    }
                });
            }
        }
        try {
            for (Future future : SimpleStaticThreadPool.getInstance().invokeAll(tasks, FLOWTIMEOUT, TimeUnit.MILLISECONDS)) {
                if (future.isCancelled()) {
                    listResult.setStatus("TIMEOUT");
                    logger.warn("rule execute timeout [" + FLOWTIMEOUT + "ms]");
                }
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        }
        for (Iterator<String> it = mapAccount.keySet().iterator(); it.hasNext(); ) {
            String sceneType = it.next();
            if (mapAccount.get(sceneType) > 0) {
                CheckResultLog riskResult = new CheckResultLog();
                riskResult.setRuleType(CheckType.ACCOUNT.toString());
                riskResult.setRuleName(sceneType);
                riskResult.setRiskLevel(mapAccount.get(sceneType));
                listResult.add(riskResult);
            }
        }
        listResult.merge(listResult_b);
        listResult.merge(listFlow);
    }

}
