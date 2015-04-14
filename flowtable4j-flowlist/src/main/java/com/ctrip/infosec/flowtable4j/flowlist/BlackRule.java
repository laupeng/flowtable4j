package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;

import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule  {
    @Override
    public boolean check(FlowFact fact, RiskResult results) {
        checkByOrderTypeMap(fact, results);
        checkAllOrderTypeMap(fact, results);
        return  true;
    }

    @Override
    public boolean checkByOrderType(OrderTypeRule rules, FlowFact fact,RiskResult results) {
        List<String> prepayType = fact.getPrepayType();
        for(String s:prepayType) {
            if (rules.byPrepay.containsKey(s)) {
                for (FlowRuleStatement rule : rules.byPrepay.get(s)) {
                    logger.debug("#######ruleId:"+rule.getRuleID()+" start check#######");
                    rule.check(fact, results);
                    logger.debug("#######ruleId:"+rule.getRuleID()+" end check#######");
                }
            }
        }
        for(FlowRuleStatement rule:rules.allPrepay){
            rule.check(fact,results);
        }
        return true;
    }
}
