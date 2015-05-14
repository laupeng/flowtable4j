package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.common.model.RiskFact;
import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.accountsecurity.RuleContent;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.dal.PayAdaptService;
import com.ctrip.infosec.flowtable4j.flowlist.FlowRuleManager;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
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
    @Qualifier("cardRiskDBInsertTemplate")
    private JdbcTemplate cardRiskDBTemplate;

    private static final long FLOWTIMEOUT = 10000;
    private static List<String> sceneTypes = new ArrayList<String>();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String EVENTWS = GlobalConfig.getString("EventWS");
    private static final String APPID = GlobalConfig.getString("APPID");
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
     * 支付校验
     *
     * @param checkEntity
     * @return
     */
    public RiskResult handle(final CheckFact checkEntity) {

        long s = System.currentTimeMillis();
        logger.debug(Utils.JSON.toJSONString(checkEntity));
        logger.debug(String.format("ReqId %d to Json elapsed %d", checkEntity.getReqId(), System.currentTimeMillis() - s));

        final RiskResult listResult_w = new RiskResult();
        final RiskResult listResult = new RiskResult();

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
                //log req
                long eps = System.currentTimeMillis() - s;
                String info = String.format("ReqId:%d, CheckWhite elapse %d ms", checkEntity.getReqId(), eps);
                CheckResultLog result = new CheckResultLog();
                result.setRuleRemark(info);
                result.setRuleName(String.valueOf(eps));
                result.setRuleType(CheckType.BW.toString());
                listResult.add(result);
                logger.debug(info);
            }
        }
        if (!isWhite) {
            parallelCheck(checkEntity, listResult);
        }
        listResult.setReqId(checkEntity.getReqId());
        //保存结果
        saveResult(listResult);
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
                        long now = System.currentTimeMillis();
                        BWManager.checkBlack(checkEntity.getBwFact(), listResult_b);
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckBlack elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.BW.toString());
                        listResult_b.add(result);
                        logger.debug(info);
                        return null;
                    }
                });
            } else if (CheckType.ACCOUNT == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        long now = System.currentTimeMillis();
                        AccountFact item = checkEntity.getAccountFact();
                        if (item != null && item.getCheckItems() != null && item.getCheckItems().size() > 0) {
                            paymentViaAccount.checkBWGRule(item, mapAccount);
                        }
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckBWGRule elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.ACCOUNT.toString());
                        listResult_b.add(result);
                        logger.debug(info);
                        return null;
                    }
                });
            } else if (CheckType.FLOWRULE == type) {
                tasks.add(new Callable() {
                    @Override
                    public Object call() {
                        long now = System.currentTimeMillis();
                        FlowFact flowFact = checkEntity.getFlowFact();
                        FlowRuleManager.check(flowFact, listFlow);
                        long eps = System.currentTimeMillis() - now;
                        String info = String.format("ReqId:%d,CheckFlowRule elapse %d ms", checkEntity.getReqId(), eps);
                        CheckResultLog result = new CheckResultLog();
                        result.setRuleRemark(info);
                        result.setRuleName(String.valueOf(eps));
                        result.setRuleType(CheckType.FLOWRULE.toString());
                        listResult_b.add(result);
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

    private void saveResult(RiskResult result) {
        final long reqId = result.getReqId();
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        for (final CheckResultLog item : result.getResults()) {
            SimpleStaticThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    cardRiskDBTemplate.execute(
                            new CallableStatementCreator() {
                                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                    String storedProc = "{call spA_InfoSecurity_CheckResult4j_i ( ?,?,?,?,?,?,?,?)}";// 调用的sql
                                    CallableStatement cs = con.prepareCall(storedProc);
                                    cs.setLong(2, reqId);
                                    cs.setString(3, item.getRuleType());
                                    cs.setInt(4, item.getRuleID());
                                    cs.setString(5, Objects.toString(item.getRuleName(), ""));
                                    cs.setInt(6, item.getRiskLevel());
                                    cs.setString(7, Objects.toString(item.getRuleRemark(), ""));
                                    cs.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                                    cs.registerOutParameter(1, Types.BIGINT);// 注册输出参数的类型
                                    return cs;
                                }
                            }, new CallableStatementCallback() {
                                public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                                    cs.execute();
                                    return null;
                                }
                            });
                }
            });
        }
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

    public void setBWGRule(List<RuleContent> rules){
        paymentViaAccount.setBWGRule(rules);
    }

    public void removeBWGRule(List<RuleContent> rules){
        paymentViaAccount.removeBWGRule(rules);
    }
}
