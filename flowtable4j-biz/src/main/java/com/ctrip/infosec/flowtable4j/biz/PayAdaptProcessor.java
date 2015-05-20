package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.dal.PayAdaptService;
import com.ctrip.infosec.flowtable4j.model.*;
import com.ctrip.infosec.flowtable4j.payAdapt.PayAdaptManager;
import com.ctrip.infosec.sars.monitor.util.Utils;
import com.ctrip.infosec.sars.util.GlobalConfig;
import com.google.common.base.Strings;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/5/19.
 */
@Component
public class PayAdaptProcessor {
    @Autowired
    private PaymentViaAccount paymentViaAccount;
    private static Logger logger = LoggerFactory.getLogger(PayAdaptProcessor.class);
    private static final String EVENTWS = GlobalConfig.getString("EventWS");
    private static final String APPID = GlobalConfig.getString("APPID");
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
     * @param checkEntity
     * @return
     */
    public PayAdaptResult handle4PayAdapt(final PayAdaptFact checkEntity) {

        PayAdaptResult result = new PayAdaptResult();
        result.setRetCode(200);
        List<PayAdaptResultItem> results = new ArrayList<PayAdaptResultItem>();

        final List<PayAdaptResultItem> bwResults4j = new ArrayList<PayAdaptResultItem>();
        final RiskResult bwResults = new RiskResult();
        final List<PayAdaptRuleResult> payRuleResults = new ArrayList<PayAdaptRuleResult>();
        final Map<String, Integer> accountResults = new HashMap<String, Integer>();

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        if (shouldBeChecked(checkEntity)) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    long start = System.currentTimeMillis();
                    getRiskFromDroolsRest(checkEntity, bwResults4j);
                    long end = System.currentTimeMillis();
                    logger.debug("callRiskFromDroolsRest costs "+(end-start)+"ms");
                    return null;
                }
            });
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    long start = System.currentTimeMillis();
                    callBWService(checkEntity, bwResults);
                    long end = System.currentTimeMillis();
                    logger.debug("callBWService costs " + (end - start) + "ms");
                    return null;
                }
            });
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    long start = System.currentTimeMillis();
                    callPayadaptService(checkEntity, payRuleResults);
                    long end = System.currentTimeMillis();
                    logger.debug("callPayadaptService costs " + (end - start) + "ms");
                    return null;
                }
            });
        }
        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                long start = System.currentTimeMillis();
                callAccountService(checkEntity, accountResults);
                long end = System.currentTimeMillis();
                logger.debug("callAccountService costs " + (end - start) + "ms");
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
            logger.error("error.", e);
        }

        long start = System.currentTimeMillis();
        mergeResult(bwResults, bwResults4j, payRuleResults, accountResults, results);
        long end = System.currentTimeMillis();
        logger.debug("mergeResult costs " + (end - start) + "ms");
        result.setPayAdaptResultItems(results);
        return result;
    }

    private boolean shouldBeChecked(PayAdaptFact checkEntity) {
        return checkEntity.getOrderID() > 0 && CtripOrderType.contain(checkEntity.getOrderType()) && PayAdaptService.shouldBeChecked();
    }

    private void getRiskFromDroolsRest(PayAdaptFact checkEntity, List<PayAdaptResultItem> bwResults4j) throws IOException {
        RiskFact req = new RiskFact();
        req.setAppId(APPID);
        req.setEventPoint("CP0001001");
        req.setRequestTime(sdf.format(new Date()));
        req.setEventBody(new HashMap<String, Object>());
        req.getEventBody().put("OrderID", checkEntity.getOrderID());
        req.getEventBody().put("OrderType", checkEntity.getOrderType());
        req.getEventBody().put("MerchantID", checkEntity.getMerchantID());
        req.getEventBody().put("UID", checkEntity.getUid());
        req.getEventBody().put("IPAddr", checkEntity.getIpAddr());
        req.getEventBody().put("DID", checkEntity.getDid());



        RiskFact resp = Utils.JSON.parseObject(Request.Post(EVENTWS).body(new StringEntity(Utils.JSON.toJSONString(req), "UTF-8")).connectTimeout(1000).socketTimeout(2000).addHeader("Accept-Language", "zh-cn,en-us;")
                .addHeader("Accept-Encoding", "utf-8").addHeader("ContentType", "application/json").execute().returnContent().asString(), RiskFact.class);
        if(resp!=null&&resp.getFinalResultGroupByScene()!=null){
            for(Iterator<String> it = resp.getFinalResultGroupByScene().keySet().iterator();it.hasNext();){
                String scene = it.next();
                Map<String,Object> groupByScene = resp.getFinalResultGroupByScene().get(scene);
                int resultLevel = Integer.parseInt(Objects.toString(groupByScene.get("riskLevel"), "0"));
                String resultType = resultLevel>=200?"B":(resultLevel>99?"M":"W");
                PayAdaptResultItem item = new PayAdaptResultItem();
                item.setResultLevel(resultLevel);
                item.setSceneType(scene);
                item.setRuleRemark(Objects.toString(groupByScene.get("riskMessage"), ""));
                item.setResultType(resultType);
                item.setResultList(new ArrayList<String>());
                bwResults4j.add(item);
            }
        }
    }

    private void callBWService(PayAdaptFact checkEntity, RiskResult riskResult) {
        MainInfoOfPayadapt mainInfoOfPayadapt = PayAdaptService.getMainInfoByTypeAndId(checkEntity.getOrderType(), checkEntity.getOrderID());
        BWFact fact = new BWFact();
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.setContent(new HashMap<String, Object>());
        fact.getOrderTypes().add(checkEntity.getOrderType());
        fact.getOrderTypes().add(0);
        fact.getContent().put("Uid", mainInfoOfPayadapt.getUid());
        fact.getContent().put("MobilePhone", mainInfoOfPayadapt.getMobilePhone());
        fact.getContent().put("ContactEmail", mainInfoOfPayadapt.getContactEmail());
        fact.getContent().put("DID", mainInfoOfPayadapt.getDid());

        if (!BWManager.checkWhite(fact, riskResult)) {
            BWManager.checkBlack(fact, riskResult);
        }
    }

    private void callPayadaptService(PayAdaptFact checkEntity, List<PayAdaptRuleResult> payRuleResults) {
        Map<String, Object> data2Check = PayAdaptService.getAdapterData(checkEntity.getOrderType(), checkEntity.getOrderID());
        //补充ip信息
        PayAdaptService.addIpInfo(checkEntity.getOrderType(), data2Check);
        //补充补充OptionName信息
        PayAdaptService.addOptionName(checkEntity.getOrderType(), data2Check);
        FlowFact fact = new FlowFact();
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.setContent(data2Check);
        fact.getOrderTypes().add(checkEntity.getOrderType());
        fact.getOrderTypes().add(0);
        PayAdaptManager.check(fact, payRuleResults);
    }

    private void callAccountService(PayAdaptFact checkEntity, Map<String, Integer> accountResults) {
        AccountFact accountFact = new AccountFact();
        accountFact.setCheckItems(new ArrayList<AccountItem>());
        for (String scene : sceneTypes) {
            if (checkEntity != null && !Strings.isNullOrEmpty(checkEntity.getUid())) {
                AccountItem item = new AccountItem();
                item.setSceneType(scene);
                item.setCheckType("UID");
                item.setCheckValue(checkEntity.getUid());
                accountFact.getCheckItems().add(item);
            }
            if (checkEntity != null && !Strings.isNullOrEmpty(checkEntity.getIpAddr())) {
                AccountItem item = new AccountItem();
                item.setSceneType(scene);
                item.setCheckType("IP");
                item.setCheckValue(checkEntity.getIpAddr());
                accountFact.getCheckItems().add(item);
            }
        }
        // 账户风控校验
        paymentViaAccount.checkBWGRule(accountFact, accountResults);
    }

    private void mergeResult(RiskResult bwResults, List<PayAdaptResultItem> bwResults4j,
                             List<PayAdaptRuleResult> payAdaptRuleResults, Map<String, Integer> accountResults, List<PayAdaptResultItem> results) {
        //黑名单中分值超过100的 记入下次需要合并的列表中。
        List<PayAdaptResultItem> blackResults = new ArrayList<PayAdaptResultItem>();
        for (CheckResultLog item : bwResults.getResults()) {
            if (item.getRiskLevel() > 100) {
                PayAdaptResultItem payAdaptResultItem = new PayAdaptResultItem();
                payAdaptResultItem.setSceneType("PAYMENT-CONF-LIPIN");
                payAdaptResultItem.setResultLevel(295);
                payAdaptResultItem.setResultList(new ArrayList<String>());
                payAdaptResultItem.getResultList().add("unable to pay");
                payAdaptResultItem.setResultType("B");
                payAdaptResultItem.setRuleRemark("支付适配黑名单规则");
                blackResults.add(payAdaptResultItem);
                break;
            }
        }
        Map<String, PayAdaptResultItem> groupByScene = new HashMap<String, PayAdaptResultItem>();
        //对 blackResults，payAdaptRuleResults，accountResults，bwResults4j按scene 分组，取每组分值最大相
        for (PayAdaptResultItem item : blackResults) {
            if (groupByScene.containsKey(item.getSceneType())) {
                if (groupByScene.get(item.getSceneType()).getResultLevel() < item.getResultLevel()) {
                    //更新该场景下的分值
                    groupByScene.get(item.getSceneType()).setResultLevel(item.getResultLevel());
                    groupByScene.get(item.getSceneType()).setSceneType(item.getSceneType());
                    groupByScene.get(item.getSceneType()).setResultList(item.getResultList());
                    groupByScene.get(item.getSceneType()).setResultType(item.getResultType());
                    groupByScene.get(item.getSceneType()).setRuleRemark(item.getRuleRemark());

                }
            } else {
                groupByScene.put(item.getSceneType(), item);
            }
        }

        for (PayAdaptRuleResult item : payAdaptRuleResults) {
            if (groupByScene.containsKey(item.getSceneType())) {
                if (groupByScene.get(item.getSceneType()).getResultLevel() < item.getRiskLevel()) {
                    groupByScene.get(item.getSceneType()).setResultLevel(item.getRiskLevel());
                    groupByScene.get(item.getSceneType()).setResultType("F");
                    if (groupByScene.get(item.getSceneType()).getResultList() == null) {
                        groupByScene.get(item.getSceneType()).setResultList(new ArrayList<String>());
                    }
                    groupByScene.get(item.getSceneType()).getResultList().add(item.getPaymentStatus());
                    groupByScene.get(item.getSceneType()).setRuleRemark(item.getRuleDesc());
                }
            } else {
                PayAdaptResultItem payAdaptResultItem = new PayAdaptResultItem();
                payAdaptResultItem.setRuleRemark(item.getRuleDesc());
                payAdaptResultItem.setResultList(new ArrayList<String>());
                payAdaptResultItem.getResultList().add(item.getPaymentStatus());
                payAdaptResultItem.setResultType("F");
                payAdaptResultItem.setResultLevel(item.getRiskLevel());
                payAdaptResultItem.setSceneType(item.getSceneType());

                groupByScene.put(item.getSceneType(), payAdaptResultItem);
            }
        }

        for (Iterator<String> it = accountResults.keySet().iterator(); it.hasNext(); ) {
            String scene = it.next();
            if (accountResults.get(scene).intValue() > 0) {
                if (groupByScene.containsKey(scene)) {
                    if (groupByScene.get(scene).getResultLevel() < accountResults.get(scene).intValue()) {
                        groupByScene.get(scene).setSceneType(scene);
                        groupByScene.get(scene).setResultLevel(accountResults.get(scene).intValue());
                    }
                } else {
                    PayAdaptResultItem payAdaptResultItem = new PayAdaptResultItem();
                    payAdaptResultItem.setResultLevel(accountResults.get(scene).intValue());
                    payAdaptResultItem.setSceneType(scene);
                    groupByScene.put(scene, payAdaptResultItem);
                }
            }
        }

        for (PayAdaptResultItem item : bwResults4j) {
            if (groupByScene.containsKey(item.getSceneType())) {
                if (groupByScene.get(item.getSceneType()).getResultLevel() < item.getResultLevel()) {
                    //更新该场景下的分值
                    groupByScene.get(item.getSceneType()).setResultLevel(item.getResultLevel());
                    groupByScene.get(item.getSceneType()).setRuleRemark(item.getRuleRemark());
                    groupByScene.get(item.getSceneType()).setResultList(item.getResultList());
                    groupByScene.get(item.getSceneType()).setSceneType(item.getSceneType());
                    groupByScene.get(item.getSceneType()).setResultType(item.getResultType());

                }
            } else {
                groupByScene.put(item.getSceneType(), item);
            }
        }
        results.addAll(groupByScene.values());
    }

}
