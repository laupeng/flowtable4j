package com.ctrip.infosec.flowtable4j.bwrule;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule {
    private static Integer THREAD_COUNT = 5;
    private ThreadPoolExecutor executor= new ThreadPoolExecutor(100,200,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(500),
             new CustomizableThreadFactory("pool-checkBlackRule-"));

    @Override
    public boolean check(BWFact fact,RiskResult results) {
        return checkEQRuleByOrderType(fact, results) | checkNEQRuleByOrderType(fact, results);
    }

    class CheckResult {
        public Integer matched =0;
    }

    @Override
    protected boolean checkEQRules(final BWFact fact, HashMap<Integer,HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQ,final RiskResult results) {
        //选择规则
        List<RuleStatement> matchedRules=new ArrayList<RuleStatement>();
        for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeEQ.containsKey(orderType)) {
                HashMap<String, HashMap<String, List<RuleStatement>>> matchRules = orderTypeEQ.get(orderType);
                for (String key : matchRules.keySet()) { //遍历所有字段
                    String val = Strings.nullToEmpty(fact.getString(key)).toLowerCase();
                    if (!Strings.isNullOrEmpty(val)) {
                        HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);
                        if (fieldRules.containsKey(val)) { //如果值的 Key 存在
                            List<RuleStatement> keyRules = fieldRules.get(val);
                            if(keyRules!=null && keyRules.size()>0){
                                matchedRules.addAll(keyRules);
                            }
                        }
                    }
                }
            }
        }
        return executeRuleStatements(fact, results, matchedRules);
    }

    private boolean executeRuleStatements(final BWFact fact, final RiskResult results,List<RuleStatement> matchedRules) {
        final CheckResult cr=new CheckResult();
        int ruleSize = matchedRules.size();
        if(ruleSize>0){
            List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
            int  splitSize = ruleSize / THREAD_COUNT; //每线程应该数目
            int  plus   = ruleSize - splitSize * THREAD_COUNT; //按splitSize+1分配的线程数
            int  normal = splitSize==0? 0:THREAD_COUNT - plus; //按splitSize分配的线程数
            int  startIndex =0;
            int  lastIndex =0;
            for(int i=0;i<normal;i++,startIndex+=splitSize){
                lastIndex  = startIndex + splitSize;
                final List<RuleStatement> ruleStatements = matchedRules.subList(startIndex,lastIndex);
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        for (RuleStatement ruleStatement : ruleStatements) {
                            if (ruleStatement.check(fact, results)) cr.matched++;
                        }
                        return null;
                    }
                });
            }
            //StartIndex继续上次循环
            splitSize = splitSize+1;
            for(int i=0;i<plus;i++,startIndex += splitSize ){
                lastIndex  = startIndex + splitSize;
                final List<RuleStatement> ruleStatements = matchedRules.subList(startIndex,lastIndex);
                tasks.add(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        for (RuleStatement ruleStatement : ruleStatements) {
                            if (ruleStatement.check(fact, results)) cr.matched++;
                        }
                        return null;
                    }
                });
            }

            try {
                executor.invokeAll(tasks,100,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cr.matched > 0;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact,HashMap<Integer,HashMap<String, List<RuleStatement>>> orderTypeNEQ, RiskResult results) {
        List<RuleStatement> matchedRules=new ArrayList<RuleStatement>();
        for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeNEQ.containsKey(orderType)) {
                HashMap<String, List<RuleStatement>> matchRules= orderTypeNEQ.get(orderType);
                for (String key : matchRules.keySet()) { //遍历所有字段
                     String val = fact.getString(key);
                     if (!Strings.isNullOrEmpty(val)) {
                          List<RuleStatement> keyRules = matchRules.get(key);
                          if(keyRules!=null && keyRules.size()>0){
                              matchedRules.addAll(keyRules);
                          }
                    }
                }
            }
        }
        return executeRuleStatements(fact, results, matchedRules);
    }
}
