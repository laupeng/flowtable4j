package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.flowtable4j.accountrule.AccountBWGManager;
import com.ctrip.infosec.flowtable4j.biz.converter.PayAdaptConverter;
import com.ctrip.infosec.flowtable4j.bwrule.BWManager;
import com.ctrip.infosec.flowtable4j.dal.PaybaseDbService;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 由于不少PD没有调用支付适配模块，依赖于订单校验模块写入PaybaseDb的内容
 * 因此需要另写一个服务仅包含 黑白名单 + 账户风控
 * Created by thyang on 2015-07-09.
 */
@Component
public class PayAdaptInner {
    @Autowired
    AccountBWGManager accountBWGManager;

    @Autowired
    BWManager bwManager;

    @Autowired
    PayAdaptConverter payAdaptConverter;

    @Autowired
    PaybaseDbService paybaseDbService;

    private static Logger logger = LoggerFactory.getLogger(PayAdaptInner.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static List<String> sceneTypes = new ArrayList<String>();
    static {
        sceneTypes.add("PAYMENT-CONF-LIPIN");
        sceneTypes.add("PAYMENT-CONF-CC");
        sceneTypes.add("PAYMENT-CONF-CCC");
        sceneTypes.add("PAYMENT-CONF-CTRIPAY");
        sceneTypes.add("CREDIT-EXCHANGE");
        sceneTypes.add("CTRIPAY-CASHOUT");
        sceneTypes.add("CASH-EXCHANGE");
        sceneTypes.add("PAYMENT-CONF-DCARD");
        sceneTypes.add("PAYMENT-CONF-ALIPAY");
        sceneTypes.add("PAYMENT-CONF-CASH");
        sceneTypes.add("PAYMENT-CONF-WEIXIN");
        sceneTypes.add("PAYMENT-CONF-EBANK");
        sceneTypes.add("CREDIT-GUARANTEE");
    }
    /**
     * 支付适配校验
     *
     * @param po
     * @return
     */
    public void handle4PayAdapt(final PO po) {

        PayAdaptResult result = new PayAdaptResult();
        result.setRetCode(200);

        List<PayAdaptResultItem> results = new ArrayList<PayAdaptResultItem>();

        final RiskResult bwResults = new RiskResult();

        final Map<String, Integer> accountResults = new HashMap<String, Integer>();

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        final Map<String,Object> productInfo = po.getProductinfo();

        if(productInfo!=null && productInfo.size()>0) {
                tasks.add(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        //调用黑白名单模块
                        long start = System.currentTimeMillis();
                        checkPaymentBWGRule(po.getOrdertype(), bwResults,productInfo);
                        long end = System.currentTimeMillis();
                        logger.debug("check Payment BWG Rule costs " + (end - start) + "ms");
                        return null;
                    }
                });
        }
        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                long start = System.currentTimeMillis();
                //调用账户风控黑白名单
                checkAccountBWGService(po.getProductinfo(), accountResults);
                long end = System.currentTimeMillis();
                logger.debug("check AccountBWG Service costs " + (end - start) + "ms");
                return null;
            }
        });
        try {
            List<Future<Object>> futures = SimpleStaticThreadPool.getInstance().invokeAll(tasks, 1, TimeUnit.SECONDS);
            for (Future future : futures) {
                if (future.isCancelled()) {
                    logger.warn("payAdapt timeout");
                    result.setRetCode(500);
                }
            }
        } catch (InterruptedException e) {
            logger.error("payment adapt error.", e);
        }

        long start = System.currentTimeMillis();
        //合并结果
        mergeResult(bwResults,accountResults, results);
        long end = System.currentTimeMillis();
        logger.debug("merge pay adapt result costs " + (end - start) + "ms");
        result.setPayAdaptResultItems(results);
        paybaseDbService.save(MapX.getString(productInfo,new String[]{"maininfo","merchantid"}), po.getOrderid(), po.getOrdertype(), MapX.getString(productInfo,new String[]{"userinfo","uid"}),results);
   }

    /**
     * 调用支付黑白名单
     * @param orderType
     * @param riskResult
     * @param productInfo
     */
    private void checkPaymentBWGRule(Integer orderType, RiskResult riskResult,Map<String,Object> productInfo) {
        BWFact fact = new BWFact();
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.setContent(new HashMap<String, Object>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(orderType);
        fact.setContent(payAdaptConverter.fillBWGCheckEntity(productInfo));
        if (!bwManager.checkWhite(fact, riskResult)) {
             bwManager.checkBlack(fact, riskResult);
        }
    }


    /**
     * 调用账户风控
     * @param productInfo
     * @param accountResults
     */
    private void checkAccountBWGService(Map<String,Object> productInfo, Map<String, Integer> accountResults) {
        AccountFact accountFact = new AccountFact();
        accountFact.setCheckItems(new ArrayList<AccountItem>());
        String uid = MapX.getString(productInfo,new String[]{"userinfo","uid"});
        String userIp = MapX.getString(productInfo,new String[]{"ipinfo","useripadd"});
        String did = MapX.getString(productInfo,new String[]{"didinfo","did"});

        for (String scene : sceneTypes) {
            if (!Strings.isNullOrEmpty(uid)) {
                AccountItem item = new AccountItem();
                item.setSceneType(scene);
                item.setCheckType("UID");
                item.setCheckValue(uid);
                accountFact.getCheckItems().add(item);
            }
            if (!Strings.isNullOrEmpty(userIp)) {
                AccountItem item = new AccountItem();
                item.setSceneType(scene);
                item.setCheckType("IP");
                item.setCheckValue(userIp);
                accountFact.getCheckItems().add(item);
            }
            if (!Strings.isNullOrEmpty(did)) {
                AccountItem item = new AccountItem();
                item.setSceneType(scene);
                item.setCheckType("DID");
                item.setCheckValue(did);
                accountFact.getCheckItems().add(item);
            }
        }
        // 账户风控校验
        accountBWGManager.checkBWGRule(accountFact, accountResults);
    }

    private void mergeResult(RiskResult bwResults,Map<String, Integer> accountResults, List<PayAdaptResultItem> results) {

        //支付风控黑白名单中，如命中计入礼品卡黑名单
        List<PayAdaptResultItem> allResults = new ArrayList<PayAdaptResultItem>();
        for (CheckResultLog item : bwResults.getResults()) {
            if (item.getRiskLevel() > 100) {
                PayAdaptResultItem payAdaptResultItem = new PayAdaptResultItem();
                payAdaptResultItem.setSceneType("PAYMENT-CONF-LIPIN");
                payAdaptResultItem.setResultLevel(295);
                payAdaptResultItem.setResultList(new ArrayList<String>());
                payAdaptResultItem.getResultList().add("unable to pay");
                payAdaptResultItem.setResultType("B");
                payAdaptResultItem.setRuleRemark("支付适配黑名单规则");
                allResults.add(payAdaptResultItem);
                break;
            }
        }

        //账户风控命中规则，转 PayAdaptResultItem
        for (Iterator<String> it = accountResults.keySet().iterator(); it.hasNext(); ) {
            String scene = it.next();
            if (accountResults.get(scene).intValue() > 0) {
                PayAdaptResultItem payAdaptResultItem = new PayAdaptResultItem();
                payAdaptResultItem.setResultLevel(accountResults.get(scene).intValue());
                payAdaptResultItem.setSceneType(scene);
                payAdaptResultItem.setResultType("R");
                payAdaptResultItem.setResultList(new ArrayList<String>());
                allResults.add(payAdaptResultItem);
            }
        }

        Map<String, PayAdaptResultItem> groupByScene = new HashMap<String, PayAdaptResultItem>();
        //按scene 分组，取每组分值最大值
        for (PayAdaptResultItem item:allResults) {
            String sceneType = item.getSceneType().toUpperCase();
            if (groupByScene.containsKey(sceneType)) {
                if (groupByScene.get(sceneType).getResultLevel() > item.getResultLevel()) {
                    continue;
                }
            }
            groupByScene.put(sceneType, item);
        }
        results.addAll(groupByScene.values());
    }
}
