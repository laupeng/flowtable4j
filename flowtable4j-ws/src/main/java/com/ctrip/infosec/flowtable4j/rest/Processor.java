package com.ctrip.infosec.flowtable4j.rest;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.accountsecurity.PaymentViaAccount;
import com.ctrip.infosec.flowtable4j.bwlist.BWManager;
import com.ctrip.infosec.flowtable4j.flowlist.FlowRuleManager;
import com.ctrip.infosec.flowtable4j.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
    @Qualifier("cardRiskDBInsertTemplate")
    private JdbcTemplate cardRiskDBTemplate;

    private static final long DBTIMEOUT = 1000;
    private static final long FLOWTIMEOUT = 1000;

    public RiskResult handle(final CheckFact checkEntity) {
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
            }
        }
        if (!isWhite) {
            parallelCheck(checkEntity, listResult);
        }
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
                        logger.info("***1:" + (System.currentTimeMillis() - now));
                        return null;
                    }
                });
            } else if (CheckType.ACCOUNT == type) {
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
            } else if (CheckType.FLOWRULE == type) {
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
        try {
            for (Future future : SimpleStaticThreadPool.getInstance().invokeAll(tasks, FLOWTIMEOUT, TimeUnit.MILLISECONDS)) {
                if (future.isCancelled()) {
                    listResult.setStatus("TIMEOUT");
                    logger.warn("rule execute timeout [" + FLOWTIMEOUT + "ms]");
                }
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException",e);
        }
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

    private void saveResult(RiskResult result) {
        final String sql = "INSERT INTO dbo.InfoSecurity_CheckResult4j (ReqID, RuleType, RuleID, RuleName, RiskLevel,RuleRemark, CreateDate)" +
                "VALUES (?,?,?,?,?,?,?)";
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
                                    cs.setDate(8, new Date(System.currentTimeMillis()));

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
//        SimpleStaticThreadPool.invokeAll(tasks, DBTIMEOUT, TimeUnit.MILLISECONDS);
    }
}
