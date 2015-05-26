package com.ctrip.infosec.flowtable4j.biz;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.flowtable4j.accountsecurity.AccountBWGRuleHandle;
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
    private AccountBWGRuleHandle accountBWGRuleHandle;
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
        final List<PayAdaptResultItem> payRuleResults = new ArrayList<PayAdaptResultItem>();
        final Map<String, Integer> accountResults = new HashMap<String, Integer>();

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        //支付适配流量规则是否开启
        if (isPayAdaptFlowRuleDefined(checkEntity)) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    //调用反欺诈平台
                    long start = System.currentTimeMillis();
                    checkRiskByDroolsEngine(checkEntity, bwResults4j);
                    long end = System.currentTimeMillis();
                    logger.debug("get RiskResult by Drools Engine costs "+(end-start)+"ms");
                    return null;
                }
            });
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    //调用黑白名单模块
                    long start = System.currentTimeMillis();
                    checkPaymentBWGRule(checkEntity, bwResults);
                    long end = System.currentTimeMillis();
                    logger.debug("check Payment BWG Rule costs " + (end - start) + "ms");
                    return null;
                }
            });
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    long start = System.currentTimeMillis();
                    //调用支付适配流量规则
                    checkPayAdaptFlowRule(checkEntity, payRuleResults);
                    long end = System.currentTimeMillis();
                    logger.debug("check PayAdapt FlowRule costs " + (end - start) + "ms");
                    return null;
                }
            });
        }
        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                long start = System.currentTimeMillis();
                //调用账户风控黑白名单
                checkAccountBWGService(checkEntity, accountResults);
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
        mergeResult(bwResults, bwResults4j, payRuleResults, accountResults, results);
        long end = System.currentTimeMillis();
        logger.debug("merge pay adapt result costs " + (end - start) + "ms");
        result.setPayAdaptResultItems(results);
        return result;
    }

    private boolean isPayAdaptFlowRuleDefined(PayAdaptFact checkEntity) {
        return checkEntity.getOrderID() > 0 && CtripOrderType.contain(checkEntity.getOrderType()) && PayAdaptService.isPayAdapatFlowRuleOpen();
    }

    /**
     * 调用佰云的反欺诈平台
     * @param checkEntity
     * @param bwResults4j
     * @throws IOException
     */
    private void checkRiskByDroolsEngine(PayAdaptFact checkEntity, List<PayAdaptResultItem> bwResults4j) throws IOException {
        RiskFact req = new RiskFact();
        req.setAppId(APPID);
        req.setEventPoint("CP0001001"); //支付适配固定值
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
                String resultType = resultLevel>=200? "B":(resultLevel>99? "M":"W");
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

    private void checkPaymentBWGRule(PayAdaptFact checkEntity, RiskResult riskResult) {
        BWFact fact = new BWFact();
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.setContent(new HashMap<String, Object>());
        fact.getOrderTypes().add(checkEntity.getOrderType());
        fact.getOrderTypes().add(0);
        fact.setContent(PayAdaptService.fillBWGCheckEntity(checkEntity.getOrderType(), checkEntity.getOrderID()));
        if (!BWManager.checkWhite(fact, riskResult)) {
            BWManager.checkBlack(fact, riskResult);
        }
    }

    private void checkPayAdaptFlowRule(PayAdaptFact checkEntity, List<PayAdaptResultItem> payRuleResults) {

        Map<String, Object> data2Check = PayAdaptService.fillPayAdaptCheckEntity(checkEntity.getOrderType(), checkEntity.getOrderID());

        //补充ip信息
        PayAdaptService.getIPInfo(checkEntity.getOrderType(), data2Check);

        //补充补充OptionName信息
        PayAdaptService.getOptions(checkEntity.getOrderType(), data2Check);

        FlowFact fact = new FlowFact();
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(checkEntity.getOrderType());
        fact.getOrderTypes().add(0);
        fact.setContent(data2Check);
        PayAdaptManager.check(fact, payRuleResults);
    }

    private void checkAccountBWGService(PayAdaptFact checkEntity, Map<String, Integer> accountResults) {
        AccountFact accountFact = new AccountFact();
        accountFact.setCheckItems(new ArrayList<AccountItem>());
        String uid="";
        String userIp="";
        if (checkEntity != null)
        {
            uid = checkEntity.getUid();
            userIp = checkEntity.getIpAddr();
        }
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
        }
        // 账户风控校验
        accountBWGRuleHandle.checkBWGRule(accountFact, accountResults);
    }

    private void mergeResult(RiskResult bwResults, List<PayAdaptResultItem> bwResults4j,
                             List<PayAdaptResultItem> payAdaptRuleResults, Map<String, Integer> accountResults, List<PayAdaptResultItem> results) {

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

        allResults.addAll(bwResults4j);
        allResults.addAll(payAdaptRuleResults);
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
